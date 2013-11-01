package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import java.util.List;

/**
 * Created by julien on 24/10/13
 */
public abstract class ExpandableDTOAdapter<T, E extends ExpandableListItem<T>, V extends DTOView<E>> extends DTOAdapter<E, V>
{
    private List<T> underlyingItems;

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


    public void setUnderlyingItems(List<T> underlyingItems)
    {
        this.underlyingItems = underlyingItems;
    }

    public void addUnderlyingItem(T item)
    {
        if (this.underlyingItems != null)
        {
            this.underlyingItems.add(item);
        }
    }

    @Override public int getCount()
    {
        return underlyingItems != null ? underlyingItems.size() : 0;
    }

    @Override public Object getItem(int i)
    {
        Object wrappedItem = super.getItem(i);
        if (wrappedItem == null)
        {
            T underlyingItem = underlyingItems != null ? underlyingItems.get(i) : null;
            if (underlyingItem == null)
            {
                return null;
            }
            wrappedItem = wrap(underlyingItem);
            if (items != null)
            {
                items.set(i, (E) wrappedItem);
            }
        }
        return wrappedItem;
    }

    protected E wrap(T underlyingItem)
    {
        throw new RuntimeException("wrap method is not implemented");
    }
}
