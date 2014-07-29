package com.tradehero.th.persistence.translation;

import dagger.Module;

@Module(
        injects = {
                UserTranslationSettingPreferenceTest.class,
        },
        library = true,
        complete = false,
        overrides = true
)
public class PersistenceTranslationTestModule
{
}
