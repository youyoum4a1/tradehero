package com.ayondo.academy.persistence;

import com.ayondo.academy.persistence.prefs.PreferenceModule;
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
