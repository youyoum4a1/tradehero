package com.tradehero.th.fragments.translation;

import dagger.Component;

/**
 * Created by tho on 9/10/2014.
 */
@Component
public interface FragmentTranslationComponent
{
    void injectTranslatableLanguageListFragment(TranslatableLanguageListFragment target);
}
