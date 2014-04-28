package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.api.DTOView;
import java.util.Collection;

abstract public class ViewDTOSetAdapter<T, ViewType extends DTOView<T>>
        extends DTOSetAdapter<T>
{
    protected final LayoutInflater inflater;

    //<editor-fold desc="Constructors">
    public ViewDTOSetAdapter(Context context, LayoutInflater inflater)
    {
        super(context);
        this.inflater = inflater;
    }

    public ViewDTOSetAdapter(Context context, LayoutInflater inflater, Collection<T> objects)
    {
        super(context, objects);
        this.inflater = inflater;
    }
    //</editor-fold>

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = inflater.inflate(getViewResId(position), parent, false);
        }

        ViewType dtoView = (ViewType) convertView;
        fineTune(position, getItem(position), dtoView);

        return convertView;
    }

    abstract protected int getViewResId(int position);

    protected void fineTune(int position, T dto, ViewType dtoView)
    {
        dtoView.display(dto);
    }
}
