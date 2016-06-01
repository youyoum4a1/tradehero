package com.ayondo.academy.models.share;

import dagger.Module;

@Module(
        injects = {
                SocialShareTranslationHelperTest.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public class ModelsShareUITestModule
{
}
