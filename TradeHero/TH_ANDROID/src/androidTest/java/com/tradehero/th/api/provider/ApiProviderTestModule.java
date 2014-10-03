package com.tradehero.th.api.provider;

import dagger.Module;

@Module(
        injects = {
                ProviderDTODeserialiserTest.class,
        },
        complete = false,
        library = true
)
public class ApiProviderTestModule
{
}
