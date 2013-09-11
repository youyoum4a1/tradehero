package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.LinkedList;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/11/13 Time: 11:28 AM Copyright (c) TradeHero */
public abstract class DTOAdapter<T> extends BaseAdapter
{
    protected final LayoutInflater inflater;

    protected final Context context;

    private List<T> items;

    public DTOAdapter(Context context, LayoutInflater inflater)
    {
        super();
        this.context = context;
        this.inflater = inflater;
    }

    public void update(List<T> items)
    {
        if (this.items == null)
        {
            this.items = new LinkedList<>();
        }
        this.items.addAll(items);
    }

    @Override public int getCount()
    {
        return items.size();
    }

    @Override public Object getItem(int i)
    {
        return items.get(i);
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @Override public abstract View getView(int position, View convertView, ViewGroup viewGroup);
}
