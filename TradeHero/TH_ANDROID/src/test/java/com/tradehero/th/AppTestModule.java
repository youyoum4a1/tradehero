package com.tradehero.th;

import com.tradehero.th.activities.ActivityTestModule;
import com.tradehero.th.api.ApiTestModule;
import com.tradehero.th.fragments.FragmentTestModule;
import com.tradehero.th.models.ModelsTestModule;
import com.tradehero.th.persistence.PersistenceTestModule;
import dagger.Module;

@Module(
        includes = {
                ApiTestModule.class,
                ModelsTestModule.class,
                ActivityTestModule.class,
                FragmentTestModule.class,
                PersistenceTestModule.class
        },
        complete = false,
        library = true
)
public class AppTestModule
{
}
