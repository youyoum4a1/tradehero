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
    //<editor-fold desc="Constructors">
    public ViewDTOSetAdapter(Context context)
    {
        super(context);
    }

    public ViewDTOSetAdapter(Context context, Collection<T> objects)
    {
        super(context, objects);
    }
    //</editor-fold>

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(getViewResId(position), parent, false);
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
