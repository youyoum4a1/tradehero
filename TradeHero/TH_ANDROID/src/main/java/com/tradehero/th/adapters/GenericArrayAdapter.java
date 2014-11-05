package com.tradehero.th.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.Collections;
import java.util.List;
import android.support.annotation.NonNull;

/**
 * Created by thonguyen on 29/10/14.
 */
public abstract class GenericArrayAdapter<T> extends BaseAdapter
{
    @NonNull protected List<T> items = Collections.emptyList();
    @NonNull private final LayoutInflater inflater;
    @NonNull private final Context context;
    @LayoutRes private final int layoutResourceId;

    public GenericArrayAdapter(@NonNull Context context, @LayoutRes int layoutResourceId)
    {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.layoutResourceId = layoutResourceId;
    }

    public void setItems(@NonNull List<T> items)
    {
        this.items = items;
        notifyDataSetChanged();
    }

    public void clear()
    {
        items.clear();
    }

    @Override public int getCount()
    {
        return items.size();
    }

    @Override public Object getItem(int i)
    {
        return items.get(i);
    }

    protected View conditionalInflate(int position, View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = inflater.inflate(layoutResourceId, viewGroup, false);
        }
        return convertView;
    }

    @Override public long getItemId(int position)
    {
        return position;
    }

    @NonNull public final Context getContext()
    {
        return context;
    }

    @NonNull public final LayoutInflater getInflater()
    {
        return inflater;
    }
}
