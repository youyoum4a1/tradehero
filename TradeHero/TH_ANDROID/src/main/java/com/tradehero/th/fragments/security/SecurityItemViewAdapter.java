package com.ayondo.academy.fragments.security;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Filter;
import com.tradehero.common.widget.filter.ListCharSequencePredicateFilter;
import com.ayondo.academy.adapters.ArrayDTOAdapter;
import com.ayondo.academy.api.DTOView;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.filter.security.SecurityCompactDTOFilter;
import com.ayondo.academy.inject.HierarchyInjector;
import java.util.List;

abstract public class SecurityItemViewAdapter<SecurityDTOType extends SecurityCompactDTO>
        extends ArrayDTOAdapter<SecurityDTOType, DTOView<SecurityDTOType>>
{
    protected List<SecurityDTOType> originalItems;

    //<editor-fold desc="Constructors">
    public SecurityItemViewAdapter(Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
        HierarchyInjector.inject(context, this);
    }
    //</editor-fold>

    @Override public void setItems(@NonNull List<SecurityDTOType> items)
    {
        originalItems = items;
        setItemsToShow(getPredicateFilter().filter(items));
    }

    protected void setItemsToShow(@NonNull List<SecurityDTOType> showItems)
    {
        super.setItems(showItems);
    }

    abstract public ListCharSequencePredicateFilter<SecurityDTOType> getPredicateFilter();
    abstract public Filter getFilter();

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public long getItemId(int position)
    {
        Object item = getItem(position);
        return item == null ? 0 : item.hashCode();
    }

    protected class SecurityItemFilter
            extends SecurityCompactDTOFilter<SecurityDTOType>
    {
        public SecurityItemFilter(ListCharSequencePredicateFilter<SecurityDTOType> predicateFilter)
        {
            super(predicateFilter);
        }

        @Override protected Filter.FilterResults performFiltering(CharSequence charSequence)
        {

            return performFiltering(charSequence, originalItems);
        }

        @Override protected void publishResults(CharSequence charSequence, SecurityFilterResults<SecurityDTOType> filterResults)
        {
            if(filterResults.castedValues != null)
            {
                setItemsToShow(filterResults.castedValues);
                notifyDataSetChanged();
            }
        }
    }
}