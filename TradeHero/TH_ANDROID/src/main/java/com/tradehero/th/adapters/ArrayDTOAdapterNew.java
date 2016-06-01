package com.ayondo.academy.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.ayondo.academy.api.DTOView;
import java.util.List;

public class ArrayDTOAdapterNew<
        DTOType,
        ViewType extends View & DTOView<DTOType>>
    extends DTOAdapterNew<DTOType>
{
    //<editor-fold desc="Constructors">
    public ArrayDTOAdapterNew(
            @NonNull Context context,
            @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
    }

    public ArrayDTOAdapterNew(
            @NonNull Context context,
            @LayoutRes int layoutResourceId,
            @NonNull List<DTOType> objects)
    {
        super(context, layoutResourceId, objects);
    }
    //</editor-fold>

    @Override public ViewType getView(int position, View convertView, ViewGroup viewGroup)
    {
        //noinspection unchecked
        return (ViewType) super.getView(position, convertView, viewGroup);
    }

    @NonNull @Override protected ViewType inflate(int position, ViewGroup viewGroup)
    {
        //noinspection unchecked
        return (ViewType) super.inflate(position, viewGroup);
    }
}
