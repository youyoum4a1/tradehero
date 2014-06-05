package com.tradehero.common;

import com.tradehero.common.utils.UtilsModule;
import dagger.Module;

@Module(
        includes = {
                UtilsModule.class
        },
        complete = false,
        library = true
)
public class CommonModule
{
}
