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
public abstract class ExpandableDTOAdapter<
            DTOType,
            WrappedDTOType extends ExpandableListItem<DTOType>,
            DTOViewType extends DTOView<WrappedDTOType>>
        extends DTOAdapter<WrappedDTOType, DTOViewType>
{
    public static final int RES_ID_EXPANDED_LAYOUT = R.id.expanding_layout;
    private List<DTOType> underlyingItems;

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

        DTOViewType dtoView = (DTOViewType) convertView;
        WrappedDTOType expandableWrapper = (WrappedDTOType) getItem(position);

        toggleExpanded(expandableWrapper, convertView);

        dtoView.display(expandableWrapper);
        fineTune(position, expandableWrapper, dtoView);
        return convertView;
    }

    protected void toggleExpanded(WrappedDTOType expandableWrapper, View convertView)
    {
        View expandingLayout = convertView.findViewById(RES_ID_EXPANDED_LAYOUT);
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
    }

    public void setUnderlyingItems(List<DTOType> underlyingItems)
    {
        this.underlyingItems = underlyingItems;
    }

    public void addUnderlyingItem(DTOType item)
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
            DTOType underlyingItem = underlyingItems != null ? underlyingItems.get(i) : null;
            if (underlyingItem == null)
            {
                return null;
            }
            wrappedItem = wrap(underlyingItem);
            if (items != null)
            {
                items.set(i, (WrappedDTOType) wrappedItem);
            }
        }
        return wrappedItem;
    }

    protected WrappedDTOType wrap(DTOType underlyingItem)
    {
        throw new RuntimeException("wrap method is not implemented");
    }
}
