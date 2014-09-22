package com.tradehero.th.fragments.translation;

import dagger.Module;

@Module(
        injects = {
                TranslatableLanguageItemLinearViewTest.class,
                TranslatableLanguageListFragmentTest.class,
        },
        complete = false,
        library = true
)
public class FragmentTranslationTestModule
{
}
