package com.tradehero.th.models.fastfill;

import com.tradehero.th.models.jumio.JumioFastFillUtil;
import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                FastFillBuildTypeModule.class,
        },
        complete = false,
        library = true
)
public class FastFillModule
{
    @Provides FastFillUtil providesFastFillUtil(JumioFastFillUtil fastFillUtil)
    {
        return fastFillUtil;
    }
}
