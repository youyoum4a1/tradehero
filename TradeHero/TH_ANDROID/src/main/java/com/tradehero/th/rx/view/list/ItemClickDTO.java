package com.tradehero.th.rx.view.list;

import android.view.View;
import android.widget.AdapterView;

public class ItemClickDTO
{
    public final AdapterView<?> parent;
    public final View view;
    public final int position;
    public final long id;

    public ItemClickDTO(AdapterView<?> parent, View view, int position, long id)
    {
        this.parent = parent;
        this.view = view;
        this.position = position;
        this.id = id;
    }
}
