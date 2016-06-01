package com.ayondo.academy.utils.broadcast;

import android.os.Bundle;
import android.support.annotation.NonNull;

public interface BroadcastData
{
    @NonNull Bundle getArgs();
    @NonNull String getBroadcastBundleKey();
    @NonNull String getBroadcastIntentActionName();
}
