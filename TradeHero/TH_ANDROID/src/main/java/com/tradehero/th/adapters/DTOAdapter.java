package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.tradehero.th.api.DTOView;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/11/13 Time: 11:28 AM Copyright (c) TradeHero */
public abstract class DTOAdapter<T, V extends DTOView<T>> extends BaseAdapter
{
    public static final String TAG = DTOAdapter.class.getSimpleName();

    protected final LayoutInflater inflater;
    protected final Context context;
    protected final int layoutResourceId;

    private List<T> items;

    public DTOAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super();
        this.context = context;
        this.inflater = inflater;
        this.layoutResourceId = layoutResourceId;
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
        //THLog.d(TAG, "getCount");
        return items != null ? items.size() : 0;
    }

    @Override public Object getItem(int i)
    {
        //THLog.d(TAG, "getItem " + i);
        return items != null ? items.get(i) : null;
    }

    @Override public long getItemId(int i)
    {
        //THLog.d(TAG, "getItemId " + i);
        return i;
    }

    @SuppressWarnings("unchecked")
    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        //THLog.d(TAG, "getView " + position);
        if (convertView == null)
        {
            convertView = inflater.inflate(layoutResourceId, viewGroup, false);
        }

        V dtoView = (V) convertView;
        T dto = (T) getItem(position);
        dtoView.display(dto);
        fineTune(position, dto, dtoView);
        return convertView;
    }

    protected abstract void fineTune(int position, T dto, V dtoView);

    @Override public void notifyDataSetChanged()
    {
        //THLog.d(TAG, "notifyDataSetChanged");
        super.notifyDataSetChanged();
    }
}
