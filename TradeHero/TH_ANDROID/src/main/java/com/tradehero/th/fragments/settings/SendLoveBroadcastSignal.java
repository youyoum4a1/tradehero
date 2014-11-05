package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import com.tradehero.th.utils.broadcast.BroadcastData;
import android.support.annotation.NonNull;

public class SendLoveBroadcastSignal implements BroadcastData
{
    @NonNull @Override public Bundle getArgs()
    {
        return new Bundle();
    }

    @NonNull @Override public String getBroadcastBundleKey()
    {
        return FragmentSettingUIModule.KEY_SEND_LOVE_BROADCAST;
    }

    @NonNull @Override public String getBroadcastIntentActionName()
    {
        return FragmentSettingUIModule.SEND_LOVE_INTENT_ACTION_NAME;
    }
}
