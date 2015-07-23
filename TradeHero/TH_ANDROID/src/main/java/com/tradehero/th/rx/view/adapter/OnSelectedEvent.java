package com.tradehero.th.rx.view.adapter;

import android.support.annotation.NonNull;
import android.widget.AdapterView;

abstract public class OnSelectedEvent
{
    @NonNull public final AdapterView<?> parent;

    public OnSelectedEvent(@NonNull AdapterView<?> parent)
    {
        this.parent = parent;
    }
}
