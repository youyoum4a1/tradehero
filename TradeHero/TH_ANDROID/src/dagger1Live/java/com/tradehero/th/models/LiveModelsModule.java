package com.tradehero.th.models;

import com.tradehero.th.models.fastfill.FastFillModule;
import com.tradehero.th.models.sms.SMSModule;
import dagger.Module;

@Module(
        includes = {
                FastFillModule.class,
                SMSModule.class,
        },
        complete = false,
        library = true
)
public class LiveModelsModule
{
}
