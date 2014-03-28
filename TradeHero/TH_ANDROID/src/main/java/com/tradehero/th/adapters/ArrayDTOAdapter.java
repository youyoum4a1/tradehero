package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.api.DTOView;

import java.util.Collection;
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

    /**
     * If called in non-UI thread,must be synchronized(see source code of ArrayAdapter)
     * TODO
     * @param item
     */
    public void addItem(T item)
    {
        List<T> itemsCopy = items;
        if (itemsCopy != null)
        {
            itemsCopy.add(item);
        }
    }

    /**
     * If called in non-UI thread,must be synchronized
     * TODO
     * @param items
     */
    public void addItems(T[] items) {
        int len = items.length;
        for(int i=0;i<len;i++){
            addItem(items[i]);
        }
    }

    /**
     * If called in non-UI thread,must be synchronized
     * TODO
     * @param data
     */
    public void addItems(List<T> data) {
        List<T> itemsCopy = items;
        if (itemsCopy != null)
        {
            itemsCopy.addAll(data);
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
