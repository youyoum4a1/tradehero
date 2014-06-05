package com.tradehero.base;

import com.tradehero.TestModule;
import com.tradehero.th.base.Application;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestApplication extends Application
{
    @Override protected Object[] getModules()
    {
        Object[] appModules = super.getModules();
        List<Object> modules = new ArrayList<>(Arrays.asList(appModules));
        modules.add(new TestModule());
        return modules.toArray();
    }
}
