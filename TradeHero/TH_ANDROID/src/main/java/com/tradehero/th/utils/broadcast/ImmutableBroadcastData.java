package com.tradehero.th.utils.broadcast;

import android.os.Bundle;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ImmutableBroadcastData implements BroadcastData
{
    public static BroadcastData create(String broadcastBundleKey, String broadcastIntentActionName)
    {
        return create(broadcastBundleKey, broadcastIntentActionName, new Bundle());
    }

    public static BroadcastData create(String broadcastBundleKey, String broadcastIntentActionName, Bundle bundle)
    {
        return new AutoValue_ImmutableBroadcastData(broadcastBundleKey, broadcastIntentActionName, bundle);
    }

    public abstract String getBroadcastBundleKey();
    public abstract String getBroadcastIntentActionName();
    public abstract Bundle getArgs();
}
