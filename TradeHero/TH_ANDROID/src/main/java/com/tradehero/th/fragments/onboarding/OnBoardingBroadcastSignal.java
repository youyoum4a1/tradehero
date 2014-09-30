package com.tradehero.th.fragments.onboarding;

import android.os.Bundle;
import com.tradehero.th.utils.broadcast.BroadcastData;
import org.jetbrains.annotations.NotNull;

public class OnBoardingBroadcastSignal implements BroadcastData
{
    @NotNull @Override public Bundle getArgs()
    {
        return new Bundle();
    }

    @NotNull @Override public String getBroadcastBundleKey()
    {
        return FragmentOnBoardModule.KEY_ON_BOARD_BROADCAST;
    }

    @NotNull @Override public String getBroadcastIntentActionName()
    {
        return FragmentOnBoardModule.ON_BOARD_INTENT_ACTION_NAME;
    }
}
