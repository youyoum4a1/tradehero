package com.tradehero.th.models.fastfill;

import com.tradehero.th.models.fastfill.jumio.DebugNetverifyFastFillUtil;
import com.tradehero.th.models.fastfill.jumio.NetverifyFastFillUtil;
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
