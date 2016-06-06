package com.androidth.general.common.rx;

import android.app.Activity;
import android.support.annotation.NonNull;
import rx.android.lifecycle.LifecycleEvent;

public class LifecycleEventWithActivity
{
    @NonNull public final Activity activity;
    @NonNull public final LifecycleEvent event;

    //<editor-fold desc="Constructors">
    public LifecycleEventWithActivity(@NonNull Activity activity, @NonNull LifecycleEvent event)
    {
        this.event = event;
        this.activity = activity;
    }
    //</editor-fold>
}
