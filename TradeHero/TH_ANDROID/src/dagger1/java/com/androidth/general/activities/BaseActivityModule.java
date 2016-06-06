package com.androidth.general.activities;

import android.app.Activity;
import com.androidth.general.UIModule;
import com.androidth.general.utils.dagger.AppModule;
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
}
