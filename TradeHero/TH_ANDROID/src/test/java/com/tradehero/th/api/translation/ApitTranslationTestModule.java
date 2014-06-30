package com.tradehero.th.api.translation;

import com.tradehero.th.api.translation.bing.ApiTranslationBingTestModule;
import dagger.Module;

@Module(
        includes = {
                ApiTranslationBingTestModule.class,
        },
        complete = false,
        library = true
)
public class ApitTranslationTestModule
{
}
