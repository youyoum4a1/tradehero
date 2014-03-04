package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
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
        List<T> itemsCopy = items;
        if (itemsCopy != null)
        {
            itemsCopy.add(item);
        }
    }

    @Override public int getCount()
    {
        List<T> itemsCopy = items;
        return itemsCopy != null ? itemsCopy.size() : 0;
    }

    @Override public Object getItem(int i)
    {
        List<T> itemsCopy = items;
        return itemsCopy != null ? itemsCopy.get(i) : null;
    }
}
