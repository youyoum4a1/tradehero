package com.tradehero.th.rx.dialog;

import android.content.DialogInterface;
import android.support.annotation.NonNull;

public class OnDialogClickEvent
{
    @NonNull public final DialogInterface dialog;
    public final int which;

    public OnDialogClickEvent(@NonNull DialogInterface dialog, int which)
    {
        this.dialog = dialog;
        this.which = which;
    }

    public boolean isPositive()
    {
        return which == DialogInterface.BUTTON_POSITIVE;
    }

    public boolean isNegative()
    {
        return which == DialogInterface.BUTTON_NEGATIVE;
    }

    public boolean isNeutral()
    {
        return which == DialogInterface.BUTTON_NEUTRAL;
    }
}
