package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.api.DTOView;
import java.util.ArrayList;
import java.util.List;

public abstract class ArrayDTOAdapter<T, V extends DTOView<T>> extends DTOAdapter<T, V>
{
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
     * If called in non-UI thread, must be synchronized(see source code of ArrayAdapter) TODO
     */
    public void addItem(T item)
    {
        List<T> itemsCopy = items;
        if (itemsCopy == null)
        {
            itemsCopy = new ArrayList<>();
        }
        itemsCopy.add(item);
        items = itemsCopy;
    }

    /**
     * If called in non-UI thread, must be synchronized TODO
     */
    public void addItems(T[] items)
    {
        int len = items.length;
        for (int i = 0; i < len; i++)
        {
            addItem(items[i]);
        }
    }

    /**
     * If called in non-UI thread, must be synchronized TODO
     */
    public void addItems(List<T> data)
    {
        List<T> itemsCopy = items;
        if (itemsCopy != null)
        {
            itemsCopy.addAll(data);
        }
    }

    @Override public void clear()
    {
        super.clear();
        if (items != null)
        {
            items.clear();
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
