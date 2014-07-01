package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.th.api.DTOView;

public abstract class DTOAdapter<T, V extends DTOView<T>> extends ArrayAdapter
{
    protected final LayoutInflater inflater;
    protected final Context context;
    protected int layoutResourceId;

    //<editor-fold desc="Constructors">
    public DTOAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, layoutResourceId);
        this.context = context;
        this.inflater = inflater;
        this.layoutResourceId = layoutResourceId;
    }
    //</editor-fold>

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

    public void setLayoutResourceId(int layoutResourceId)
    {
        this.layoutResourceId = layoutResourceId;
    }

    protected abstract void fineTune(int position, T dto, V dtoView);

    @Override public long getItemId(int position)
    {
        return position;
    }
}
