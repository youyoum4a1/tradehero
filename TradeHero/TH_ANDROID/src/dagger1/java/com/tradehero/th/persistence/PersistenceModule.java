package com.tradehero.th.persistence;

import com.tradehero.th.persistence.prefs.PreferenceModule;
import dagger.Module;

@Module(
        includes = {
                PreferenceModule.class,
                PersistenceGameLiveModule.class,
        },
        complete = false,
        library = true
)
public class PersistenceModule
{
}
