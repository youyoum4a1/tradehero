package com.androidth.general.models;

import com.androidth.general.models.fastfill.FastFillModule;
import com.androidth.general.models.sms.twilio.SMSModule;

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
