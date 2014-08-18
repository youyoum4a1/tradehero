package com.tradehero.th.network.service;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.api.alert.AlertPlanDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.MiddleCallback;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import retrofit.Callback;
import timber.log.Timber;

import static com.tradehero.th.api.ValidMocker.mockValidParameter;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(THRobolectricTestRunner.class)
public class ServiceWrapperTest extends AbstractServiceTestBase
{
    @Inject AlertPlanServiceWrapper alertPlanServiceWrapper;

    @Test public void canGetAllServices()
    {
        int serviceCount = 27;
        int wrappersWithOutService = 1;
        assertThat(getAllServices().size()).isEqualTo(serviceCount );
        assertThat(getAllServiceAsyncs().size()).isEqualTo(serviceCount);
        assertThat(getAllServiceWrappers().size()).isEqualTo(serviceCount + wrappersWithOutService);
        assertThat(getAllServiceWrapperInjectors().size()).isEqualTo(serviceCount + wrappersWithOutService);
    }

    @Test public void canConfirmMiddleCallbackOnAlertPlanServiceAsync()
            throws InvocationTargetException, IllegalAccessException
    {
        CallbackHolder holder = new CallbackHolder();
        AlertPlanServiceAsync mockedAsync = tieCallbackMethodsToHolder(AlertPlanServiceAsync.class, holder);
        replaceServiceAsync(alertPlanServiceWrapper, mockedAsync);

        MiddleCallback<List<AlertPlanDTO>> middleCallback = alertPlanServiceWrapper.getAlertPlans(new UserBaseKey(1), mock(Callback.class));

        assertThat(holder.callback).isNotNull();
        assertThat(holder.callback).isSameAs(middleCallback);
    }

    /**
     * Making sure all methods of service wrapper that an async method pass the MiddleCallback
     * to Retrofit
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Test public void callingAllServiceWrappersWithCallbackPassesMiddleCallback()
            throws InvocationTargetException, IllegalAccessException, InstantiationException
    {
        List<Class<?>> serviceWrappers = getAllServiceWrappers();
        serviceWrappers.remove(TranslationServiceWrapper.class); // As it has no associated Async
        List<Class<?>> serviceAsyncs = getAllServiceAsyncs();
        for (int repeat = 0; repeat < 5; repeat++)
        {
            for (int index = 0; index < serviceWrappers.size(); index++)
            {
                callingServiceWrapperWithCallbackPassesMiddleCallback(
                        serviceWrappers.get(index),
                        serviceAsyncs.get(index));
            }
        }
    }

    public void callingServiceWrapperWithCallbackPassesMiddleCallback(
            @NotNull Class<?> serviceWrapperType,
            @NotNull Class<?> serviceAsyncType)
            throws InvocationTargetException, IllegalAccessException, InstantiationException
    {
        Timber.d("Wrapper %s with own Async %s", serviceWrapperType.getSimpleName(), serviceAsyncType.getSimpleName());
        Object serviceWrapper = obtainInjectableParameter(serviceWrapperType);
        for (Method wrapperCallbackMethod : getCallbackMethods(serviceWrapperType))
        {
            callingCallbackMethodPassesMiddleCallback(
                    serviceWrapper,
                    wrapperCallbackMethod,
                    serviceAsyncType);
        }
    }

    public void callingCallbackMethodPassesMiddleCallback(
            @NotNull Object serviceWrapper,
            @NotNull Method wrapperCallbackMethod,
            @NotNull Class<?> serviceAsyncType)
            throws InvocationTargetException, IllegalAccessException
    {
        Timber.d("Wrapper %s, Method %s", serviceWrapper.getClass().getSimpleName(), wrapperCallbackMethod.getName());
        CallbackHolder holder = new CallbackHolder();
        Object mockedAsync = tieCallbackMethodsToHolder(serviceAsyncType, holder);
        replaceServiceAsync(serviceWrapper, mockedAsync);

        Class<?>[] parameterTypes = wrapperCallbackMethod.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];
        for (int index = 0; index < parameterTypes.length; index++)
        {
            parameters[index] = mockValidParameter(parameterTypes[index]);
        }

        MiddleCallback middleCallback = (MiddleCallback) wrapperCallbackMethod.invoke(
                serviceWrapper,
                parameters);

        assertThat(holder.callback).isNotNull();
        assertThat(MiddleCallback.class.isAssignableFrom(holder.callback.getClass()));
        assertThat(holder.callback).isSameAs(middleCallback);
    }
}
