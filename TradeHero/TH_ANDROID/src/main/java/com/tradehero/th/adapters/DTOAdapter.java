package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.DTOView;
import java.util.LinkedList;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/11/13 Time: 11:28 AM Copyright (c) TradeHero */
public abstract class DTOAdapter<T, V extends DTOView<T>> extends BaseAdapter
{
    public static final String TAG = DTOAdapter.class.getSimpleName();

    protected final LayoutInflater inflater;
    protected final Context context;
    private final int layoutResourceId;

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

    @Override public int getCount()
    {
        THLog.d(TAG, "getCount");
        return items != null ? items.size() : 0;
    }

    @Override public Object getItem(int i)
    {
        THLog.d(TAG, "getItem " + i);
        return items != null ? items.get(i) : null;
    }

    @Override public long getItemId(int i)
    {
        THLog.d(TAG, "getItemId " + i);
        return i;
    }

    @SuppressWarnings("unchecked")
    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        THLog.d(TAG, "getView " + position);
        if (convertView == null)
        {
            convertView = inflater.inflate(layoutResourceId, viewGroup, false);
        }

        //try
        //{
            V view = (V) convertView;
            view.display((T) getItem(position));
            return getView(position, view);
        //}
        //catch (Exception ex)
        //{
        //    throw new IllegalArgumentException("layoutResourceId is not match with class: " + convertView.getClass().getSimpleName() + ":" + ex.getMessage());
        //}
    }

    protected abstract View getView(int position, V convertView);

    @Override public void notifyDataSetChanged()
    {
        THLog.d(TAG, "notifyDataSetChanged");
        super.notifyDataSetChanged();
    }
}
