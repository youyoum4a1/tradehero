package com.androidth.general.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.androidth.general.api.DTOView;

public class ArrayDTOAdapter<T, V extends DTOView<T>> extends GenericArrayAdapter<T>
{
    //<editor-fold desc="Constructors">
    public ArrayDTOAdapter(@NonNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
    //</editor-fold>

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
