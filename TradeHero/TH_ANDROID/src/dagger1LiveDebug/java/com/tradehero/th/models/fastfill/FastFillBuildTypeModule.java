package com.ayondo.academy.models.fastfill;

import com.ayondo.academy.models.fastfill.jumio.DebugNetverifyFastFillUtil;
import com.ayondo.academy.models.fastfill.jumio.NetverifyFastFillUtil;
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
