package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import com.tradehero.th.fragments.onboarding.FragmentOnBoardModule;
import com.tradehero.th.utils.broadcast.BroadcastData;
import org.jetbrains.annotations.NotNull;

public class CompetitionEnrollmentBroadcastSignal implements BroadcastData
{
    @NotNull @Override public Bundle getArgs()
    {
        return new Bundle();
    }

    @NotNull @Override public String getBroadcastBundleKey()
    {
        return FragmentOnBoardModule.KEY_ENROLLMENT_BROADCAST;
    }

    @NotNull @Override public String getBroadcastIntentActionName()
    {
        return FragmentOnBoardModule.ENROLLMENT_INTENT_ACTION_NAME;
    }
}