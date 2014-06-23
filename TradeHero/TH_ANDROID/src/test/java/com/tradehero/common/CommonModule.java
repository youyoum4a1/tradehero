package com.tradehero.common;

import com.tradehero.common.persistence.PersistenceTestModule;
import com.tradehero.common.utils.UtilsTestModule;
import dagger.Module;

@Module(
        includes = {
                PersistenceTestModule.class,
                UtilsTestModule.class
        },
        complete = false,
        library = true
)
public class CommonModule
{
}
