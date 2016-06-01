package com.ayondo.academy;

import com.ayondo.academy.api.GooglePlayApiTestModule;
import dagger.Module;

@Module(
        includes = {
                GooglePlayApiTestModule.class,
        },
        complete = false,
        library = true
)
public class GooglePlayAppTestModule
{
}
