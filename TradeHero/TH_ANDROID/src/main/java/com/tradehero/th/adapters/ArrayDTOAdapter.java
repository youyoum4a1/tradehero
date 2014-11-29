package com.tradehero.th.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.api.DTOView;

public class ArrayDTOAdapter<T, V extends DTOView<T>> extends GenericArrayAdapter<T>
{
    public ArrayDTOAdapter(@NonNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        convertView = super.getView(position, convertView, viewGroup);

        @SuppressWarnings("unchecked")
        V dtoView = (V) convertView;
        @SuppressWarnings("unchecked")
        T dto = (T) getItem(position);
        dtoView.display(dto);
        fineTune(position, dto, dtoView);
        return convertView;
    }

    protected void fineTune(int position, T dto, V dtoView)
    {
        // to be overwritten
    }
}
