package com.tradehero.th.api.i18n;

import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                LanguageCodePredicateTest.class,
                LanguageDTOFactoryTest.class,
                LanguageDTOMapTest.class,
        },
        complete = false,
        library = true
)
public class ApiI18nTestModule
{
}
