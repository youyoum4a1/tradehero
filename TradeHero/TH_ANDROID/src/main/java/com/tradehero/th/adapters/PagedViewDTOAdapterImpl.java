package com.tradehero.th.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.api.DTOView;

public class PagedViewDTOAdapterImpl<
        DTOType,
        ViewType extends View & DTOView<DTOType>>
        extends PagedDTOAdapterImpl<DTOType>
{
    //<editor-fold desc="Constructors">
    public PagedViewDTOAdapterImpl(@NonNull Context context, @LayoutRes int resource)
    {
        super(context, resource);
    }
    //</editor-fold>

    @Override public ViewType getView(int position, View convertView, ViewGroup viewGroup)
    {
        //noinspection unchecked
        return (ViewType) super.getView(position, convertView, viewGroup);
    }
}
