package com.tradehero.th.rx.view;

import android.widget.CompoundButton;
import org.jetbrains.annotations.NotNull;
import rx.functions.Func1;

public class CompoundButtonIsCheckedFunc1 implements Func1<CompoundButton, Boolean>
{
    @Override public Boolean call(@NotNull CompoundButton compoundButton)
    {
        return compoundButton.isChecked();
    }
}
