package com.tradehero.th.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.tradehero.th.api.DTOView;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public abstract class ArrayDTOAdapter<T, V extends DTOView<T>> extends BaseAdapter
{
    @NotNull protected List<T> items = Collections.emptyList();
    @NotNull protected final LayoutInflater inflater;
    @NotNull private final Context context;
    @LayoutRes protected final int layoutResourceId;

    public ArrayDTOAdapter(
            @NotNull Context context,
            @LayoutRes int layoutResourceId)
    {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.layoutResourceId = layoutResourceId;
    }

    public void setItems(@NotNull List<T> items)
    {
        this.items = items;
    }

    /**
     * If called in non-UI thread, must be synchronized(see source code of ArrayAdapter) TODO
     */
    public void addItem(T item)
    {
        items.add(item);
    }

    /**
     * If called in non-UI thread, must be synchronized TODO
     */
    public void addItems(T[] items)
    {
        for (T item : items)
        {
            addItem(item);
        }
    }

    /**
     * If called in non-UI thread, must be synchronized TODO
     */
    public void addItems(List<T> data)
    {
        items.addAll(data);
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

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        convertView = conditionalInflate(position, convertView, viewGroup);

        @SuppressWarnings("unchecked")
        V dtoView = (V) convertView;
        @SuppressWarnings("unchecked")
        T dto = (T) getItem(position);
        dtoView.display(dto);
        fineTune(position, dto, dtoView);
        return convertView;
    }

    protected View conditionalInflate(int position, View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = inflater.inflate(layoutResourceId, viewGroup, false);
        }
        return convertView;
    }

    protected abstract void fineTune(int position, T dto, V dtoView);

    @Override public long getItemId(int position)
    {
        return position;
    }

    @NotNull public Context getContext()
    {
        return context;
    }
}
