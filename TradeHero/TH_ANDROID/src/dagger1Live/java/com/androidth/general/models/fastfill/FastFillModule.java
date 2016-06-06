package com.androidth.general.models.fastfill;

import com.androidth.general.models.fastfill.jumio.NetverifyFastFillUtil;
import com.androidth.general.models.fastfill.jumio.NetverifyModule;
import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
//                FastFillBuildTypeModule.class,
                NetverifyModule.class,
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
