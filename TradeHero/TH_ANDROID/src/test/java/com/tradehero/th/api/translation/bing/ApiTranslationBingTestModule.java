package com.ayondo.academy.api.translation.bing;

import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                BingLanguageDTOFactoryTest.class,
                BingTranslationTokenTest.class,
                BingUserTranslationSettingDTOTest.class,
        },
        complete = false,
        library = true
)
public class ApiTranslationBingTestModule
{
}
