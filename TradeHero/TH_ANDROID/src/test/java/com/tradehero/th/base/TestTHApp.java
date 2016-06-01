package com.ayondo.academy.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import com.facebook.LoginActivity;
import com.tradehero.TestModule;
import com.tradehero.common.log.SystemOutTree;
import dagger.internal.loaders.GeneratedAdapters;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.TestLifecycleApplication;
import org.robolectric.res.builder.RobolectricPackageManager;
import timber.log.Timber;

import static org.robolectric.Shadows.shadowOf;

public class TestTHApp extends THApp
        implements TestLifecycleApplication
{
    int mb = 1024*1024;

    //Getting the runtime reference from system
    Runtime runtime = Runtime.getRuntime();

    @Override protected void init()
    {
        super.init();

        mockFacebookLoginActivity();
    }

    private void mockFacebookLoginActivity()
    {
        RobolectricPackageManager packageManager = (RobolectricPackageManager) shadowOf(RuntimeEnvironment.application).getPackageManager();
        Intent intent = new Intent(context(), LoginActivity.class);

        ResolveInfo info = new ResolveInfo();
        info.isDefault = true;
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.packageName = RuntimeEnvironment.application.getPackageName();
        info.activityInfo = new ActivityInfo();
        info.activityInfo.applicationInfo = applicationInfo;
        info.activityInfo.name = LoginActivity.class.getName();
        packageManager.addResolveInfoForIntent(intent, info);
    }

    // Find a better way as we plant multiple ones
    @NonNull @Override protected Timber.Tree createTimberTree()
    {
        return new SystemOutTree();
    }

    @Override protected Object[] getModules()
    {
        Object[] appModules = super.getModules();
        List<Object> modules = new ArrayList<>(Arrays.asList(appModules));
        modules.add(new TestModule());
        return modules.toArray();
    }

    @Override public void beforeTest(Method method)
    {
        System.err.println("Before "
                + method.getDeclaringClass().getCanonicalName()
                + "."
                + method.getName()
                + " free "
                + runtime.freeMemory() / mb);
    }

    @Override public void prepareTest(Object test)
    {
        injectIfNecessary(test);
    }

    // Work perfectly, since jUnit does not perform in testObject context
    // instead it create a new object for each test (method block)
    private void injectIfNecessary(Object test)
    {
        try
        {
            Class.forName(test.getClass().getName() + GeneratedAdapters.INJECT_ADAPTER_SUFFIX);
            inject(test);
        }
        catch (ClassNotFoundException e)
        {
            // not a subject for injection
        }
        catch (IllegalArgumentException e)
        {
            // Need in-class inject
            System.out.println("Need further injection " + e.getStackTrace());
        }
    }

    @Override public void afterTest(Method method)
    {
        System.err.println("After "
                + method.getDeclaringClass().getCanonicalName()
                + "."
                + method.getName()
                + " free "
                + runtime.freeMemory() / mb);
    }

    protected void printMemory()
    {
        System.out.println("##### Heap utilization statistics [MB] #####");

        //Print used memory
        System.out.println("Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        //Print free memory
        System.out.println("Free Memory:"
                + runtime.freeMemory() / mb);

        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);

        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);
    }

    public static void staticInject(Object test)
    {
        get(context()).inject(test);
    }
}
