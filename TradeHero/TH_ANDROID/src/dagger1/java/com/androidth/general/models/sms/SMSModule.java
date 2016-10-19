package com.androidth.general.models.sms;

import com.androidth.general.models.sms.nexmo.NexmoModule;
import com.androidth.general.models.sms.nexmo.NexmoServiceWrapper;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                NexmoModule.class,
        },
        complete = false,
        library = true
)
public class SMSModule
{
    @Provides
    SMSServiceWrapper provideSMSServiceWrapper(NexmoServiceWrapper serviceWrapper)
    {
        return serviceWrapper;
    }
}
