package com.tradehero.th.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.api.DTOView;

public class PagedViewDTOAdapter<
        DTOType,
        ViewType extends View & DTOView<DTOType>>
        extends PagedDTOAdapter<DTOType>
{
    //<editor-fold desc="Constructors">
    public PagedViewDTOAdapter(Context context, int resource)
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
