package com.tradehero.common.fragment;

import android.content.Intent;

public class ActivityResultDTO
{
    public final int requestCode;
    public final int resultCode;
    public final Intent data;

    //<editor-fold desc="Constructors">
    public ActivityResultDTO(int requestCode, int resultCode, Intent data)
    {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }
    //</editor-fold>
}
