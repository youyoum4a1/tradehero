package com.tradehero.th.utils.broadcast;

import android.os.Bundle;
import org.jetbrains.annotations.NotNull;

public interface BroadcastData
{
    @NotNull Bundle getArgs();
    @NotNull String getBroadcastBundleKey();
    @NotNull String getBroadcastIntentActionName();
}
