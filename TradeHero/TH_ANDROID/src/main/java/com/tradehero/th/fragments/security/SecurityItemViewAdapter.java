package com.tradehero.th.fragments.security;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Filter;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.widget.filter.ListCharSequencePredicateFilter;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.filter.security.SecurityCompactDTOFilter;
import com.tradehero.th.utils.DaggerUtils;
import java.util.List;

abstract public class SecurityItemViewAdapter<SecurityCompactDTOType extends SecurityCompactDTO>
        extends ArrayDTOAdapter<SecurityCompactDTOType, SecurityItemView<SecurityCompactDTOType>>
{
    private final static String TAG = SecurityItemViewAdapter.class.getSimpleName();

    protected List<SecurityCompactDTOType> originalItems;

    //<editor-fold desc="Constructors">
    public SecurityItemViewAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
        DaggerUtils.inject(this);
    }
    //</editor-fold>

    @Override public void setItems(List<SecurityCompactDTOType> items)
    {
        originalItems = items;
        setItemsToShow(getPredicateFilter().filter(items));
    }

    protected void setItemsToShow(List<SecurityCompactDTOType> showItems)
    {
        THLog.d(TAG, "setItemsToShow " + (showItems == null ? "null" : showItems.size()));
        super.setItems(showItems);
    }

    abstract public ListCharSequencePredicateFilter<SecurityCompactDTOType> getPredicateFilter();
    abstract public Filter getFilter();

    @Override protected void fineTune(int position, SecurityCompactDTOType securityCompact, final SecurityItemView<SecurityCompactDTOType> dtoView)
    {
        // Nothing to do
    }

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
            extends SecurityCompactDTOFilter<SecurityCompactDTOType>
    {
        public SecurityItemFilter(ListCharSequencePredicateFilter<SecurityCompactDTOType> predicateFilter)
        {
            super(predicateFilter);
        }

        @Override protected Filter.FilterResults performFiltering(CharSequence charSequence)
        {

            return performFiltering(charSequence, originalItems);
        }

        @Override protected void publishResults(CharSequence charSequence, SecurityFilterResults<SecurityCompactDTOType> filterResults)
        {
            setItemsToShow(filterResults.castedValues);
            notifyDataSetChanged();
        }
    }
}