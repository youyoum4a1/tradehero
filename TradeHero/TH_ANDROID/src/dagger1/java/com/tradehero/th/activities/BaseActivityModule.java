package com.tradehero.th.activities;

import android.app.Activity;
import com.tradehero.common.widget.CustomDrawerToggle;
import com.tradehero.th.UIModule;
import com.tradehero.th.utils.dagger.AppModule;
import dagger.Module;
import dagger.Provides;

@Module(
        addsTo = AppModule.class,
        includes = UIModule.class,
        library = true,
        complete = false
) class BaseActivityModule
{
    BaseActivity activity;

    public BaseActivityModule(BaseActivity activity)
    {
        this.activity = activity;
    }

    @Provides Activity provideActivity()
    {
        return activity;
    }

    @Provides CustomDrawerToggle provideDrawerToggle()
    {
        return null;
    }
}
