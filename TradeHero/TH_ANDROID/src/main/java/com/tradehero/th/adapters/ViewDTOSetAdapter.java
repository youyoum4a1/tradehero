package com.tradehero.th.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.api.DTOView;
import java.util.Collection;
import java.util.Comparator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(getViewResId(position), parent, false);
        }

        //noinspection unchecked
        ViewType dtoView = (ViewType) convertView;
        fineTune(position, getItem(position), dtoView);

        return dtoView;
    }

    @LayoutRes abstract protected int getViewResId(int position);

    protected void fineTune(int position, T dto, ViewType dtoView)
    {
        dtoView.display(dto);
    }
}
