package com.tradehero.th.rx.dialog;

import android.content.DialogInterface;
import android.util.Pair;
import rx.functions.Func1;

public class AlertButtonClickedFilterFunc1 implements Func1<Pair<DialogInterface, Integer>, Boolean>
{
    private final int buttonId;

    //<editor-fold desc="Constructors">
    public AlertButtonClickedFilterFunc1(int buttonId)
    {
        if (buttonId != DialogInterface.BUTTON_POSITIVE
                && buttonId != DialogInterface.BUTTON_NEGATIVE)
        {
            throw new IllegalArgumentException("Invalid buttonId " + buttonId);
        }
        this.buttonId = buttonId;
    }
    //</editor-fold>

    @Override public Boolean call(Pair<DialogInterface, Integer> dialogInterfaceIntegerPair)
    {
        return dialogInterfaceIntegerPair.second == buttonId;
    }
}
