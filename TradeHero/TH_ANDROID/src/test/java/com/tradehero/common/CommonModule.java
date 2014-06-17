package com.tradehero.common;

import com.tradehero.common.persistence.PersistenceTestModule;
import com.tradehero.common.utils.UtilsModule;
import dagger.Module;

@Module(
        includes = {
                PersistenceTestModule.class,
                UtilsModule.class
        },
        complete = false,
        library = true
)
public class CommonModule
{
}
