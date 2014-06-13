package com.tradehero.th.models;

import com.tradehero.th.models.push.PushTestModule;
import dagger.Module;

@Module(
        includes = {
                PushTestModule.class
        },
        complete = false,
        library = true
)
public class ModelsTestModule
{
}
