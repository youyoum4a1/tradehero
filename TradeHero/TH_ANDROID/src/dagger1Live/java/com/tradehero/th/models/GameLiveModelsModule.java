package com.ayondo.academy.models;

import com.ayondo.academy.models.fastfill.FastFillModule;
import com.ayondo.academy.models.sms.SMSModule;
import dagger.Module;

@Module(
        includes = {
                FastFillModule.class,
                SMSModule.class,
        },
        complete = false,
        library = true
)
public class GameLiveModelsModule
{
}
