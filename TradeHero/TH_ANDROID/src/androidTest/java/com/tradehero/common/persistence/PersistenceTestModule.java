package com.tradehero.common.persistence;

import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                DTOCacheGetOrFetchTaskTest.class,
                DTOCacheRxGetOrFetchTaskTest.class,
        },
        complete = false,
        library = true
)
public class PersistenceTestModule
{
}
