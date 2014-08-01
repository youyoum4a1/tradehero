package com.tradehero.th.persistence;

import com.tradehero.th.persistence.security.PersistenceSecurityTestModule;
import com.tradehero.th.persistence.translation.PersistenceTranslationTestModule;
import dagger.Module;

@Module(
        includes = {
                PersistenceSecurityTestModule.class,
                PersistenceTranslationTestModule.class,
        },
        library = true,
        complete = false,
        overrides = true
)
public class PersistenceTestModule
{
}
