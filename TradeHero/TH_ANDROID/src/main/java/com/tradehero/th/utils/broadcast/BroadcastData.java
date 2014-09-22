package com.tradehero.th.utils.broadcast;

import android.os.Bundle;

public interface BroadcastData
{
    Bundle getArgs();
    String getBroadcastBundleKey();
    String getBroadcastIntentActionName();
}
