package com.androidth.general.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.androidth.general.api.DTOView;
import java.util.Collection;
import java.util.Comparator;

abstract public class ViewDTOSetAdapter<T, ViewType extends View & DTOView<T>>
        extends DTOSetAdapter<T>
{
    //<editor-fold desc="Constructors">
    public ViewDTOSetAdapter(@NonNull Context context)
    {
        super(context);
    }

    public ViewDTOSetAdapter(@NonNull Context context, @Nullable Comparator<T> comparator)
    {
        super(context, comparator);
    }

    public ViewDTOSetAdapter(@NonNull Context context, @Nullable Collection<T> objects)
    {
        super(context, objects);
    }

    public ViewDTOSetAdapter(@NonNull Context context, @Nullable Comparator<T> comparator, @Nullable Collection<T> objects)
    {
        super(context, comparator, objects);
    }
    //</editor-fold>

    @Override public ViewType getView(int position, @Nullable View convertView, ViewGroup parent)
    {
        ViewType dtoView;
        if (convertView == null)
        {
            dtoView = inflate(position, parent);
        }
        else
        {
            //noinspection unchecked
            dtoView = (ViewType) convertView;
        }
        dtoView.display(getItem(position));
        return dtoView;
    }

    protected ViewType inflate(int position, ViewGroup parent)
    {
        //noinspection unchecked
        return (ViewType) LayoutInflater.from(context).inflate(getViewResId(position), parent, false);
    }

    @LayoutRes abstract protected int getViewResId(int position);
}
