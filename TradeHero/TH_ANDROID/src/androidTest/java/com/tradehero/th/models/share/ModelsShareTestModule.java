package com.tradehero.th.models.share;

import dagger.Module;

@Module(
        injects = {
                SocialShareTranslationHelperTest.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public class ModelsShareTestModule
{
}
