package com.tradehero.th;

import com.tradehero.th.api.SamsungApiTestModule;
import dagger.Module;

@Module(
        includes = {
                SamsungApiTestModule.class,
        },
        complete = false,
        library = true
)
public class SamsungAppTestModule
{
}
