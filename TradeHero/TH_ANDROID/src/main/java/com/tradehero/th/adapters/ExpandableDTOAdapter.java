package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;

/**
 * Created by julien on 24/10/13
 */
public abstract class ExpandableDTOAdapter<T, E extends ExpandableListItem<T>, V extends DTOView<E>> extends DTOAdapter<E, V>
{

    public ExpandableDTOAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        //THLog.d(TAG, "getView " + position);
        if (convertView == null)
        {
            convertView = inflater.inflate(layoutResourceId, viewGroup, false);
        }

        V dtoView = (V) convertView;
        E expandableWrapper = (E) getItem(position);

        View expandingLayout = convertView.findViewById(R.id.expanding_layout);
        if (expandingLayout != null)
        {
            if (!expandableWrapper.isExpanded())
            {
                expandingLayout.setVisibility(View.GONE);
            }
            else
            {
                expandingLayout.setVisibility(View.VISIBLE);
            }
        }

        dtoView.display(expandableWrapper);
        fineTune(position, expandableWrapper, dtoView);
        return convertView;
    }

}
