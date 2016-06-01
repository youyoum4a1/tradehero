package com.ayondo.academy.fragments.onboarding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.ayondo.academy.utils.broadcast.BroadcastConstants;
import com.ayondo.academy.utils.broadcast.BroadcastData;

public class OnBoardingBroadcastSignal implements BroadcastData
{
    @NonNull @Override public Bundle getArgs()
    {
        return new Bundle();
    }

    @NonNull @Override public String getBroadcastBundleKey()
    {
        return BroadcastConstants.KEY_ON_BOARD_BROADCAST;
    }

    @NonNull @Override public String getBroadcastIntentActionName()
    {
        return BroadcastConstants.ON_BOARD_INTENT_ACTION_NAME;
    }
}
