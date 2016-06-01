package com.ayondo.academy.activities;

import android.app.Activity;
import com.ayondo.academy.UIModule;
import com.ayondo.academy.utils.dagger.AppModule;
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
