package com.tradehero.th.activities;

import com.tradehero.th.models.fastfill.FastFillUtil;
import com.tradehero.th.models.jumio.JumioFastFillUtil;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                LiveActivityUtil.class,
                SignUpLiveActivity.class,
                IdentityPromptActivity.class
        },
        complete = false,
        library = true
)
public class ActivityGameLiveModule
{
    @Provides FastFillUtil providesFastFillUtil(JumioFastFillUtil fastFillUtil)
    {
        return fastFillUtil;
    }
}
