package com.ayondo.academy;

import com.ayondo.academy.api.AmazonApiTestModule;
import dagger.Module;

@Module(
        includes = {
                AmazonApiTestModule.class,
        },
        complete = false,
        library = true
)
public class AmazonAppTestModule
{
}
