package com.ayondo.academy.rx.view.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;

public class OnItemSelectedEvent extends OnSelectedEvent
{
    @NonNull public final View view;
    public final int position;
    public final long id;

    public OnItemSelectedEvent(
            @NonNull AdapterView<?> parent,
            @NonNull View view,
            int position,
            long id)
    {
        super(parent);
        this.view = view;
        this.position = position;
        this.id = id;
    }
}
