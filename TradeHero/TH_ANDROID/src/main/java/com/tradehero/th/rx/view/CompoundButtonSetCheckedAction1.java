package com.tradehero.th.rx.view;

import android.widget.CompoundButton;
import org.jetbrains.annotations.NotNull;
import rx.functions.Action1;

public class CompoundButtonSetCheckedAction1
        implements Action1<CompoundButton>
{
    private final boolean value;

    //<editor-fold desc="Constructors">
    public CompoundButtonSetCheckedAction1(boolean value)
    {
        this.value = value;
    }
    //</editor-fold>

    @Override public void call(@NotNull CompoundButton compoundButton)
    {
        compoundButton.setChecked(value);
    }
}
