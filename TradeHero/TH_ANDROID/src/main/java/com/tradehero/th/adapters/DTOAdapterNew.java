package com.ayondo.academy.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.ayondo.academy.api.DTOView;
import java.util.List;

public class DTOAdapterNew<DTOType>
        extends ArrayAdapter<DTOType>
{
    protected static final int DEFAULT_VIEW_TYPE = 0;

    @LayoutRes protected int layoutResourceId;
    @NonNull protected LayoutInflater inflater;

    //<editor-fold desc="Constructors">
    public DTOAdapterNew(@NonNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.inflater = LayoutInflater.from(context);
    }

    public DTOAdapterNew(@NonNull Context context, int layoutResourceId, @NonNull List<DTOType> objects)
    {
        super(context, layoutResourceId, objects);
        this.layoutResourceId = layoutResourceId;
        this.inflater = LayoutInflater.from(context);
    }
    //</editor-fold>

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = inflate(position, viewGroup);
        }

        //noinspection unchecked
        DTOView<DTOType> dtoView = (DTOView<DTOType>) convertView;
        dtoView.display(getItem(position));
        return convertView;
    }

    @NonNull protected View inflate(int position, ViewGroup viewGroup)
    {
        return inflater.inflate(getViewResId(position), viewGroup, false);
    }

    public void setLayoutResourceId(int layoutResourceId)
    {
        this.layoutResourceId = layoutResourceId;
    }

    @Override public int getViewTypeCount()
    {
        return 1;
    }

    @Override public int getItemViewType(int position)
    {
        return DEFAULT_VIEW_TYPE;
    }

    @LayoutRes public int getViewResId(@SuppressWarnings("UnusedParameters") int position)
    {
        return layoutResourceId;
    }
}
