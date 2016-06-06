package com.androidth.general.fragments.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.utils.broadcast.BroadcastConstants;
import com.androidth.general.utils.broadcast.BroadcastData;

public class SendLoveBroadcastSignal implements BroadcastData
{
    @NonNull @Override public Bundle getArgs()
    {
        return new Bundle();
    }

    @NonNull @Override public String getBroadcastBundleKey()
    {
        return BroadcastConstants.KEY_SEND_LOVE_BROADCAST;
    }

    @NonNull @Override public String getBroadcastIntentActionName()
    {
        return BroadcastConstants.SEND_LOVE_INTENT_ACTION_NAME;
    }
}
