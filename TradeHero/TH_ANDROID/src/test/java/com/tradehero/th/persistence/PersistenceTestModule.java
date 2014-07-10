package com.tradehero.th.persistence;

import com.tradehero.th.persistence.security.PersistenceSecurityTestModule;
import dagger.Module;

@Module(
        includes = {
                PersistenceSecurityTestModule.class,
        },
        library = true,
        complete = false,
        overrides = true
)
public class PersistenceTestModule
{
}
