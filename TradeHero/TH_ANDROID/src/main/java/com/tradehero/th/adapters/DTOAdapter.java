package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.tradehero.th.api.DTOView;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/11/13 Time: 11:28 AM Copyright (c) TradeHero */
public abstract class DTOAdapter<T, V extends DTOView<T>> extends BaseAdapter
{
    public static final String TAG = DTOAdapter.class.getSimpleName();

    protected final LayoutInflater inflater;
    protected final Context context;
    protected int layoutResourceId;
    protected List<T> items;
    private Comparator<T> skuDetailsComparator;

    public DTOAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super();
        this.context = context;
        this.inflater = inflater;
        this.layoutResourceId = layoutResourceId;
    }

    public void setItems(List<T> items)
    {
        if (skuDetailsComparator != null)
        {
            Collections.sort(items, skuDetailsComparator);
        }
        this.items = items;
    }

    public void addItem(T item)
    {
        if (this.items != null)
        {
            this.items.add(item);
        }
    }

    public Comparator<T> getSkuDetailsComparator()
    {
        return skuDetailsComparator;
    }

    public void setSkuDetailsComparator(Comparator<T> skuDetailsComparator)
    {
        this.skuDetailsComparator = skuDetailsComparator;
    }

    @Override public int getCount()
    {
        return items != null ? items.size() : 0;
    }

    @Override public Object getItem(int i)
    {
        return items != null ? items.get(i) : null;
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @SuppressWarnings("unchecked")
    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        //THLog.d(TAG, "getView " + position);
        convertView = conditionalInflate(convertView, viewGroup);

        V dtoView = (V) convertView;
        T dto = (T) getItem(position);
        dtoView.display(dto);
        fineTune(position, dto, dtoView);
        return convertView;
    }

    protected View conditionalInflate(View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = inflater.inflate(layoutResourceId, viewGroup, false);
        }
        return convertView;
    }

    public void setLayoutResourceId(int layoutResourceId)
    {
        this.layoutResourceId = layoutResourceId;
    }

    protected abstract void fineTune(int position, T dto, V dtoView);
}
