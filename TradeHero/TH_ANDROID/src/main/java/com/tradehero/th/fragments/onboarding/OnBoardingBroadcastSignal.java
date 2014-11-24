package com.tradehero.th.fragments.onboarding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.th.utils.broadcast.BroadcastData;

public class OnBoardingBroadcastSignal implements BroadcastData
{
    @NonNull @Override public Bundle getArgs()
    {
        return new Bundle();
    }

    @NonNull @Override public String getBroadcastBundleKey()
    {
        return FragmentOnBoardModule.KEY_ON_BOARD_BROADCAST;
    }

    @NonNull @Override public String getBroadcastIntentActionName()
    {
        return FragmentOnBoardModule.ON_BOARD_INTENT_ACTION_NAME;
    }
}
