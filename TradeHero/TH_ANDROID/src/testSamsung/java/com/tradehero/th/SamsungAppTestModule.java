package com.ayondo.academy;

import com.ayondo.academy.api.SamsungApiTestModule;
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
