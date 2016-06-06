package com.androidth.general.models.sms;

import com.androidth.general.models.sms.twilio.TwilioModule;
import com.androidth.general.models.sms.twilio.TwilioServiceWrapper;
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
    @Provides SMSServiceWrapper provideSMSServiceWrapper(TwilioServiceWrapper serviceWrapper)
    {
        return serviceWrapper;
    }
}
