package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import com.tradehero.th.utils.broadcast.BroadcastData;
import org.jetbrains.annotations.NotNull;

public class SendLoveBroadcastSignal implements BroadcastData
{
    @NotNull @Override public Bundle getArgs()
    {
        return new Bundle();
    }

    @NotNull @Override public String getBroadcastBundleKey()
    {
        return FragmentSettingUIModule.KEY_SEND_LOVE_BROADCAST;
    }

    @NotNull @Override public String getBroadcastIntentActionName()
    {
        return FragmentSettingUIModule.SEND_LOVE_INTENT_ACTION_NAME;
    }
}
