package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.th.api.DTOView;

public class ArrayDTOAdapterNew<
        DTOType,
        ViewType extends View & DTOView<DTOType>>
    extends ArrayAdapter<DTOType>
{
    protected static final int DEFAULT_VIEW_TYPE = 0;

    protected int layoutResourceId;

    public ArrayDTOAdapterNew(Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
    }

    @SuppressWarnings("unchecked")
    @Override public ViewType getView(int position, View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(getViewResId(position), viewGroup, false);
        }

        ViewType dtoView = (ViewType) convertView;
        dtoView.display(getItem(position));
        return dtoView;
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

    public int getViewResId(int position)
    {
        return layoutResourceId;
    }
}
