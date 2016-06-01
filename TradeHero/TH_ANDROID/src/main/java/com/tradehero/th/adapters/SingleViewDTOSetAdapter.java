package com.ayondo.academy.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.ayondo.academy.api.DTOView;
import java.util.Collection;
import java.util.Comparator;

public class SingleViewDTOSetAdapter<T, ViewType extends View & DTOView<T>> extends ViewDTOSetAdapter<T, ViewType>
{
    @LayoutRes private int layoutResourceId;

    //<editor-fold desc="Constructors">
    public SingleViewDTOSetAdapter(@NonNull Context context,
            @LayoutRes int layoutResourceId)
    {
        super(context);
        this.layoutResourceId = layoutResourceId;
    }

    public SingleViewDTOSetAdapter(@NonNull Context context,
            @Nullable Comparator<T> comparator,
            @LayoutRes int layoutResourceId)
    {
        super(context, comparator);
        this.layoutResourceId = layoutResourceId;
    }

    public SingleViewDTOSetAdapter(@NonNull Context context,
            @LayoutRes int layoutResourceId,
            @Nullable Collection<T> objects)
    {
        super(context, objects);
        this.layoutResourceId = layoutResourceId;
    }

    public SingleViewDTOSetAdapter(@NonNull Context context,
            @Nullable Comparator<T> comparator,
            @LayoutRes int layoutResourceId,
            @Nullable Collection<T> objects)
    {
        super(context, comparator, objects);
        this.layoutResourceId = layoutResourceId;
    }
    //</editor-fold>

    @Override @LayoutRes protected int getViewResId(int position)
    {
        return layoutResourceId;
    }
}
