package com.tradehero.th.api.provider;

import dagger.Module;

@Module(
        injects = {
                ProviderCompactDTODeserialiserTest.class,
                ProviderDTODeserialiserTest.class,
        },
        complete = false,
        library = true
)
public class ApiProviderTestModule
{
}
