package com.tradehero.th.base;

import com.tradehero.TestModule;
import com.tradehero.th.utils.DaggerUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.robolectric.TestLifecycleApplication;

public class TestApplication extends Application  implements TestLifecycleApplication
{
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
        DaggerUtils.inject(test);
    }

    @Override public void afterTest(Method method)
    {
    }
}
