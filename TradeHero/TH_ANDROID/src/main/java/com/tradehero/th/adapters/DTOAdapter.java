package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.th.api.DTOView;

/**
 * Created with IntelliJ IDEA. User: tho Date: 12/10/13 Time: 4:07 PM Copyright (c) TradeHero
 */
public abstract class DTOAdapter<T, V extends DTOView<T>> extends ArrayAdapter
{
    protected final LayoutInflater inflater;
    protected final Context context;
    protected int layoutResourceId;

    public DTOAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, layoutResourceId);
        this.context = context;
        this.inflater = inflater;
        this.layoutResourceId = layoutResourceId;
    }

    @SuppressWarnings("unchecked")
    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        //THLog.d(TAG, "getView " + position);
        convertView = conditionalInflate(position, convertView, viewGroup);

        V dtoView = (V) convertView;
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
