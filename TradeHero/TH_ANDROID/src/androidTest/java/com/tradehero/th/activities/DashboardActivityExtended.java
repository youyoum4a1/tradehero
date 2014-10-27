package com.tradehero.th.activities;

import com.tradehero.th.UITestModule;
import com.tradehero.th.utils.dagger.AppModule;
import dagger.Module;
import java.util.List;

public class DashboardActivityExtended extends DashboardActivity
{
    @Override protected List<Object> getModules()
    {
        List<Object> modules = super.getModules();
        modules.add(new DashboardActivityExtendedModule());
        return modules;
    }

    @Module(
            addsTo = AppModule.class,
            includes = {
                    UITestModule.class
            },
            library = true,
            complete = false,
            overrides = true
    )
    public class DashboardActivityExtendedModule
    {
    }
}
