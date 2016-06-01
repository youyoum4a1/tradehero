package com.ayondo.academy.activities;

import android.support.annotation.NonNull;
import com.ayondo.academy.UITestModule;
import com.ayondo.academy.utils.dagger.AppModule;
import dagger.Module;
import java.util.List;

public class DashboardActivityExtended extends DashboardActivity
{
    @NonNull @Override protected List<Object> getModules()
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
