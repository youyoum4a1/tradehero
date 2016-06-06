package com.androidth.general.rx.view.adapter;

import android.support.annotation.NonNull;
import android.widget.AdapterView;

public class OnNothingSelectedEvent extends OnSelectedEvent
{
    public OnNothingSelectedEvent(@NonNull AdapterView<?> parent)
    {
        super(parent);
    }
}
