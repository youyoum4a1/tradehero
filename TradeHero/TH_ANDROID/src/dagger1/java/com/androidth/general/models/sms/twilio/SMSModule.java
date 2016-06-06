package com.androidth.general.models.sms.twilio;

import com.androidth.general.models.sms.SMSServiceWrapper;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                TwilioModule.class,
        },
        complete = false,
        library = true
)
public class SMSModule
{
    @Provides
    SMSServiceWrapper provideSMSServiceWrapper(TwilioServiceWrapper serviceWrapper)
    {
        return serviceWrapper;
    }
}
