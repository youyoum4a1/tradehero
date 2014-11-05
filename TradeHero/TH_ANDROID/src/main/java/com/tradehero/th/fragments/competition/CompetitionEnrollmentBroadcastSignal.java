package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import com.tradehero.th.fragments.onboarding.FragmentOnBoardModule;
import com.tradehero.th.utils.broadcast.BroadcastData;
import android.support.annotation.NonNull;

public class CompetitionEnrollmentBroadcastSignal implements BroadcastData
{
    @NonNull @Override public Bundle getArgs()
    {
        return new Bundle();
    }

    @NonNull @Override public String getBroadcastBundleKey()
    {
        return FragmentOnBoardModule.KEY_ENROLLMENT_BROADCAST;
    }

    @NonNull @Override public String getBroadcastIntentActionName()
    {
        return FragmentOnBoardModule.ENROLLMENT_INTENT_ACTION_NAME;
    }
}