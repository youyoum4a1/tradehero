package com.tradehero.th.base;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.internal.ActionBarSherlockCompat;
import com.actionbarsherlock.internal.ActionBarSherlockNative;
import com.actionbarsherlock.internal.ActionBarSherlockRobolectric;
import com.tradehero.TestModule;
import com.tradehero.th.utils.DaggerUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.robolectric.TestLifecycleApplication;

public class TestApplication extends Application  implements TestLifecycleApplication
{
    @Override protected void init()
    {
        super.init();

        ActionBarSherlock.registerImplementation(ActionBarSherlockRobolectric.class);
        ActionBarSherlock.unregisterImplementation(ActionBarSherlockNative.class);
        ActionBarSherlock.unregisterImplementation(ActionBarSherlockCompat.class);
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
    }

    @Override public void prepareTest(Object test)
    {
        // this is not very nice, since we will inject everytime a test is call
        // it should be done before every setup instead
        DaggerUtils.inject(test);
    }

    @Override public void afterTest(Method method)
    {
    }
}
