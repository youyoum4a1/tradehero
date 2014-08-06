package com.tradehero.th.persistence.security;

import dagger.Module;

@Module(
        injects = {
                SecurityCompactListCacheTest.class,
        },
        library = true,
        complete = false,
        overrides = true
)
public class PersistenceSecurityTestModule
{
}
