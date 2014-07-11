package com.tradehero.th.api.translation.bing;

import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                BingLanguageDTOFactoryTest.class,
                BingTranslationTokenTest.class,
        },
        complete = false,
        library = true
)
public class ApiTranslationBingTestModule
{
}
