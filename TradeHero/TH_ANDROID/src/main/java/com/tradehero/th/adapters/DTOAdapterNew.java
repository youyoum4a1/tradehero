package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.th.api.DTOView;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class DTOAdapterNew<DTOType>
        extends ArrayAdapter<DTOType>
{
    protected static final int DEFAULT_VIEW_TYPE = 0;

    protected int layoutResourceId;
    private LayoutInflater inflater;

    //<editor-fold desc="Constructors">
    public DTOAdapterNew(@NotNull Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.inflater = LayoutInflater.from(context);
    }

    public DTOAdapterNew(@NotNull Context context, int layoutResourceId, @NotNull List<DTOType> objects)
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
            convertView = inflater.inflate(getViewResId(position), viewGroup, false);
        }

        //noinspection unchecked
        DTOView<DTOType> dtoView = (DTOView<DTOType>) convertView;
        dtoView.display(getItem(position));
        return convertView;
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

    public int getViewResId(@SuppressWarnings("UnusedParameters") int position)
    {
        return layoutResourceId;
    }
}
