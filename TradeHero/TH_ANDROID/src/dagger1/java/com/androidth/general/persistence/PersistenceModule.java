package com.androidth.general.persistence;

import com.androidth.general.persistence.prefs.PreferenceModule;
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
