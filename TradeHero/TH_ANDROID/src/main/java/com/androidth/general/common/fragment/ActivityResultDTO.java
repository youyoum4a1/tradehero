package com.androidth.general.common.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

public class ActivityResultDTO
{
    @NonNull public final Activity activity;
    public final int requestCode;
    public final int resultCode;
    public final Intent data;

    //<editor-fold desc="Constructors">
    public ActivityResultDTO(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        this.activity = activity;
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }
    //</editor-fold>
}
