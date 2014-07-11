package com.tradehero.th.api.translation;

import com.tradehero.th.api.translation.bing.ApiTranslationBingTestModule;
import dagger.Module;

@Module(
        includes = {
                ApiTranslationBingTestModule.class,
        },
        injects = {
                TranslatableLanguageDTOFactoryFactoryTest.class,
                UserTranslationSettingDTOFactoryTest.class,
        },
        complete = false,
        library = true
)
public class ApiTranslationTestModule
{
}
