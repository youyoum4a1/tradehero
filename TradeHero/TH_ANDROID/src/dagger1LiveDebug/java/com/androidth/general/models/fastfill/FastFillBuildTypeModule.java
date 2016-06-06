package com.androidth.general.models.fastfill;

import com.androidth.general.models.fastfill.jumio.DebugNetverifyFastFillUtil;
import com.androidth.general.models.fastfill.jumio.NetverifyFastFillUtil;
import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class FastFillBuildTypeModule
{
    @Provides NetverifyFastFillUtil providesFastFillUtil(DebugNetverifyFastFillUtil fastFillUtil)
    {
        return fastFillUtil;
    }
}
