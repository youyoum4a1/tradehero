package com.tradehero.th.activities;

import com.tradehero.th.UITestModule;
import com.tradehero.th.inject.ExInjector;
import com.tradehero.th.utils.dagger.AppModule;
import dagger.Module;

public class DashboardActivityExtended extends DashboardActivity
{
    @Override protected ExInjector loadInjector(ExInjector injector)
    {
        return super.loadInjector(injector).plus(new DashboardActivityExtendedModule());
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
