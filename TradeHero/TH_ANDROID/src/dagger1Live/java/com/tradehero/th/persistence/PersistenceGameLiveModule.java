package com.tradehero.th.persistence;

import com.tradehero.th.persistence.kyc.KYCFormOptionsCache;
import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                KYCFormOptionsCache.class, // This is a HACK to ensure that the cache instance is a singleton...
        },
        complete = false,
        library = true
)
public class PersistenceGameLiveModule
{
}
