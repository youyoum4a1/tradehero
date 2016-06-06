package com.androidth.general.rx.dialog;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import com.androidth.general.common.annotation.DialogButton;

public class OnDialogClickEvent
{
    @NonNull public final DialogInterface dialog;
    @DialogButton public final int which;

    public OnDialogClickEvent(@NonNull DialogInterface dialog, @DialogButton int which)
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
