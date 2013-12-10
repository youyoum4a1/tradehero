package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.tradehero.th.api.DTOView;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/11/13 Time: 11:28 AM Copyright (c) TradeHero */
public abstract class ArrayDTOAdapter<T, V extends DTOView<T>> extends DTOAdapter<T, V>
{
    public static final String TAG = ArrayDTOAdapter.class.getSimpleName();

    protected List<T> items;

    public ArrayDTOAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    public void setItems(List<T> items)
    {
        this.items = items;
    }

    public void addItem(T item)
    {
        if (this.items != null)
        {
            this.items.add(item);
        }
    }

    @Override public int getCount()
    {
        return items != null ? items.size() : 0;
    }

    @Override public Object getItem(int i)
    {
        return items != null ? items.get(i) : null;
    }
}
