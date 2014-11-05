package com.tradehero.th.rx.view;

import android.widget.CompoundButton;
import android.support.annotation.NonNull;
import rx.functions.Func1;

public class CompoundButtonIsCheckedFunc1 implements Func1<CompoundButton, Boolean>
{
    @Override public Boolean call(@NonNull CompoundButton compoundButton)
    {
        return compoundButton.isChecked();
    }
}
