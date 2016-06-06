package com.androidth.general.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenericArrayAdapter<T> extends BaseAdapter
{
    @NonNull protected List<T> items = Collections.emptyList();
    @NonNull private final LayoutInflater inflater;
    @NonNull private final Context context;
    @LayoutRes private final int layoutResourceId;

    //<editor-fold desc="Constructors">
    public GenericArrayAdapter(@NonNull Context context, @LayoutRes int layoutResourceId)
    {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.layoutResourceId = layoutResourceId;
        this.items = new ArrayList<>();
    }
    //</editor-fold>

    public void setItems(@NonNull List<T> items)
    {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        return conditionalInflate(position, convertView, parent);
    }

    @Override public int getCount()
    {
        return items.size();
    }

    @Override public Object getItem(int i)
    {
        return items.get(i);
    }

    @NonNull protected View conditionalInflate(int position, View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = inflate(position, viewGroup);
        }
        return convertView;
    }

    @NonNull protected View inflate(int position, ViewGroup viewGroup)
    {
        return inflater.inflate(layoutResourceId, viewGroup, false);
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
