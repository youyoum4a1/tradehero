package com.tradehero.th.activities;

import com.tradehero.th.UITestModule;
import com.tradehero.th.inject.ExInjector;
import com.tradehero.th.utils.dagger.AppModule;
import dagger.Module;

public class SplashActivityExtended extends SplashActivity
{
    @Override protected ExInjector loadInjector(ExInjector injector)
    {
        return super.loadInjector(injector).plus(new SplashActivityExtendedModule());
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
    public class SplashActivityExtendedModule
    {
    }

}
