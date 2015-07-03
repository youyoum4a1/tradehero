package com.tradehero.th.models.sms;

import com.tradehero.th.models.sms.twilio.TwilioModule;
import com.tradehero.th.models.sms.twilio.TwilioServiceWrapper;
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
