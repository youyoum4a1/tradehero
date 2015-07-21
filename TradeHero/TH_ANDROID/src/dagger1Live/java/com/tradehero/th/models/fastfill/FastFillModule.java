package com.tradehero.th.models.fastfill;

import com.tradehero.th.models.fastfill.jumio.NetverifyFastFillUtil;
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
    @Provides FastFillUtil providesFastFillUtil(NetverifyFastFillUtil fastFillUtil)
    {
        return fastFillUtil;
    }
}
