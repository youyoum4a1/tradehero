package com.ayondo.academy.api.translation;

import com.ayondo.academy.api.translation.bing.ApiTranslationBingTestModule;
import dagger.Module;

@Module(
        includes = {
                ApiTranslationBingTestModule.class,
        },
        injects = {
                TranslatableLanguageDTOFactoryFactoryTest.class,
                UserTranslationSettingDTOFactoryTest.class,
                UserTranslationSettingDTOTest.class,
        },
        complete = false,
        library = true
)
public class ApiTranslationTestModule
{
}
