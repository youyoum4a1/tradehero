package com.tradehero.th.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.api.DTOView;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class ArrayDTOAdapterNew<
        DTOType,
        ViewType extends View & DTOView<DTOType>>
    extends DTOAdapterNew<DTOType>
{
    //<editor-fold desc="Constructors">
    public ArrayDTOAdapterNew(
            @NotNull Context context,
            @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
    }

    public ArrayDTOAdapterNew(
            @NotNull Context context,
            @LayoutRes int layoutResourceId,
            @NotNull List<DTOType> objects)
    {
        super(context, layoutResourceId, objects);
    }
    //</editor-fold>

    @Override public ViewType getView(int position, View convertView, ViewGroup viewGroup)
    {
        //noinspection unchecked
        return (ViewType) super.getView(position, convertView, viewGroup);
    }
}
