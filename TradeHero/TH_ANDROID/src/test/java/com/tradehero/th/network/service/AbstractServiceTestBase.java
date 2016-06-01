package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import com.android.internal.util.Predicate;
import com.tradehero.util.TestUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import retrofit.Callback;
import timber.log.Timber;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract public class AbstractServiceTestBase
{
    //<editor-fold desc="Get Services By Type">
    public ArrayList<Class<?>> getAllServices()
    {
        return TestUtil.getClassesForPackage(
                AlertPlanServiceRx.class.getPackage(),
                new Predicate<Class<?>>()
                {
                    @Override public boolean apply(Class<?> aClass)
                    {
                        return isService(aClass);
                    }
                });
    }

    public boolean isService(Class<?> aClass)
    {
        return aClass.isInterface() && aClass.getSimpleName().matches("^.*Service$");
    }

    public ArrayList<Class<?>> getAllServiceAsyncs()
    {
        return TestUtil.getClassesForPackage(
                AlertPlanServiceRx.class.getPackage(),
                new Predicate<Class<?>>()
                {
                    @Override public boolean apply(Class<?> aClass)
                    {
                        return isServiceAsync(aClass);
                    }
                });
    }

    public boolean isServiceAsync(@NonNull Class<?> aClass)
    {
        return aClass.isInterface() && aClass.getSimpleName().matches("^.*ServiceAsync$");
    }

    public ArrayList<Class<?>> getAllServiceWrappers()
    {
        return TestUtil.getClassesForPackage(
                AlertPlanServiceRx.class.getPackage(),
                new Predicate<Class<?>>()
                {
                    @Override public boolean apply(Class<?> aClass)
                    {
                        return isServiceWrapper(aClass);
                    }
                });
    }

    public boolean isServiceWrapper(@NonNull Class<?> aClass)
    {
        return !aClass.isInterface() && aClass.getSimpleName().matches("^.*ServiceWrapper$");
    }

    public ArrayList<Class<?>> getAllServiceWrapperInjectors()
    {
        return TestUtil.getClassesForPackage(
                AlertPlanServiceRx.class.getPackage(),
                new Predicate<Class<?>>()
                {
                    @Override public boolean apply(Class<?> aClass)
                    {
                        return isServiceWrapperInjector(aClass);
                    }
                });
    }

    public boolean isServiceWrapperInjector(@NonNull Class<?> aClass)
    {
        return !aClass.isInterface() && aClass.getSimpleName().matches("^.*ServiceWrapper\\$\\$InjectAdapter$");
    }
    //</editor-fold>

    //<editor-fold desc="Identify Methods that take Callbacks">
    @NonNull public List<Method> getCallbackMethods(@NonNull Class<?> service)
    {
        List<Method> methodsWithCallback = new ArrayList<>();
        Method [] declared = service.getDeclaredMethods();
        for (Method method : declared)
        {
            if (usesCallback(method))
            {
                methodsWithCallback.add(method);
            }
        }
        return methodsWithCallback;
    }

    public boolean usesCallback(@NonNull Method method)
    {
        Class<?>[] parameters = method.getParameterTypes();
        return parameters.length > 0 &&
                parameters[parameters.length - 1].isAssignableFrom(Callback.class);
    }
    //</editor-fold>

    //<editor-fold desc="List @Inject programmatically">
    public Object obtainInjectableParameter(@NonNull Class<?> anyType)
    {
        //return DaggerUtils.getObject(anyType);
    }
    //</editor-fold>

    public void replaceServiceAsync(Object serviceWrapper, Object withAsyncService)
            throws IllegalAccessException
    {
        Field[] fields = serviceWrapper.getClass().getDeclaredFields();
        for (Field field :fields)
        {
            if (field.getType().isAssignableFrom(withAsyncService.getClass()))
            //if (isServiceAsync(field.getType()))
            {
                Timber.d("Replacing %s's %s field with a %s",
                        serviceWrapper.getClass().getSimpleName(),
                        field.getName(),
                        withAsyncService.getClass().getSimpleName());
                field.setAccessible(true);
                field.set(serviceWrapper, withAsyncService);
            }
            else
            {
                Timber.d("Did not replace %s's %s field with a %s",
                        serviceWrapper.getClass().getSimpleName(),
                        field.getName(),
                        withAsyncService.getClass().getSimpleName());
            }
        }
    }

    public<T> T tieCallbackMethodsToHolder(
            @NonNull Class<T> serviceAsyncType,
            @NonNull final CallbackHolder holder)
            throws InvocationTargetException, IllegalAccessException
    {
        T service = mock(serviceAsyncType);
        List<Method> callbackMethods = getCallbackMethods(serviceAsyncType);
        for (Method callbackMethod: callbackMethods)
        {
            Timber.d("Tying %s.%s", serviceAsyncType.getSimpleName(), callbackMethod.getName());
            Class<?>[] parameterTypes = callbackMethod.getParameterTypes();
            Object[] parameters = new Object[parameterTypes.length];
            for (int index = 0; index < parameterTypes.length; index++)
            {
                parameters[index] = any(parameterTypes[index]);
            }
            when(callbackMethod.invoke(service, parameters)).then(new Answer<Object>()
            {
                @Override public Object answer(InvocationOnMock invocation) throws Throwable
                {
                    Object[] args = invocation.getArguments();
                    Callback lastArg = (Callback) args[args.length - 1];
                    holder.callback = lastArg;
                    return lastArg;
                }
            });
        }
        return service;
    }
}
