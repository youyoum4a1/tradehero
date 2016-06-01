package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.api.ValidMocker;
import com.ayondo.academy.base.TestTHApp;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import timber.log.Timber;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ServiceWrapperTest extends AbstractServiceTestBase
{
    @Inject AlertPlanServiceWrapper alertPlanServiceWrapper;
    @Inject ValidMocker validMocker;

    @Before
    public void setUp()
    {
        TestTHApp.staticInject(this);
    }

    @Test public void canGetAllServices()
    {
        int serviceCount = 4;
        int wrappersWithOutService = 1;
        //for (Class<?> service : getAllServices())
        //{
        //    Timber.d("%s", service);
        //}
        assertThat(getAllServices().size()).isEqualTo(serviceCount);
        //for (Class<?> serviceAsync : getAllServiceAsyncs())
        //{
        //    Timber.d("%s", serviceAsync);
        //}
        assertThat(getAllServiceAsyncs().size()).isEqualTo(serviceCount);
        //for (Class<?> wrapper : getAllServiceWrappers())
        //{
        //    Timber.d("%s", wrapper);
        //}
        assertThat(getAllServiceWrappers().size()).isEqualTo(serviceCount + wrappersWithOutService);
        //for (Class<?> injector : getAllServiceWrapperInjectors())
        //{
        //    Timber.d("%s", injector);
        //}
        assertThat(getAllServiceWrapperInjectors().size()).isEqualTo(serviceCount + wrappersWithOutService);
    }

    @Test public void canConfirmMiddleCallbackOnAlertPlanServiceAsync()
            throws InvocationTargetException, IllegalAccessException
    {
        CallbackHolder holder = new CallbackHolder();
        AlertPlanServiceRx mockedAsync = tieCallbackMethodsToHolder(AlertPlanServiceRx.class, holder);
        replaceServiceAsync(alertPlanServiceWrapper, mockedAsync);

        //MiddleCallback<List<AlertPlanDTO>> middleCallback = alertPlanServiceWrapper.getAlertPlans(new UserBaseKey(1), mock(Callback.class));
        //
        //assertThat(middleCallback).isNotNull();
        //assertThat(holder.callback).isNotNull();
        //assertThat(holder.callback).isSameAs(middleCallback);
    }

    /**
     * Making sure all methods of service wrapper that an async method pass the MiddleCallback
     * to Retrofit
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Test @Ignore // No longer a test
    public void callingAllServiceWrappersWithCallbackPassesMiddleCallback()
            throws InvocationTargetException, IllegalAccessException, InstantiationException, InterruptedException
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
                System.gc();
                Thread.sleep(100);
            }
        }
    }

    public void callingServiceWrapperWithCallbackPassesMiddleCallback(
            @NonNull Class<?> serviceWrapperType,
            @NonNull Class<?> serviceAsyncType)
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
            @NonNull Object serviceWrapper,
            @NonNull Method wrapperCallbackMethod,
            @NonNull Class<?> serviceAsyncType)
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
            parameters[index] = validMocker.mockValidParameter(parameterTypes[index]);
        }

        //MiddleCallback middleCallback = (MiddleCallback) wrapperCallbackMethod.invoke(
        //        serviceWrapper,
        //        parameters);
        //
        //assertThat(holder.callback).isNotNull();
        //assertThat(MiddleCallback.class.isAssignableFrom(holder.callback.getClass()));
        //assertThat(holder.callback).isSameAs(middleCallback);
    }
}
