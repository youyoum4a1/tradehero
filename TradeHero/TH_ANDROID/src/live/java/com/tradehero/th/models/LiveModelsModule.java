package com.tradehero.th.models;

import com.tradehero.th.models.sms.SMSModule;
import dagger.Module;

@Module(
        includes = {
                SMSModule.class,
        },
        complete = false,
        library = true
)
public class LiveModelsModule
{
}
