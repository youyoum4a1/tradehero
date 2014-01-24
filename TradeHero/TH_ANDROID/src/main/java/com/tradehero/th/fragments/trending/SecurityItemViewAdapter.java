package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Filter;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.widget.filter.ListCharSequencePredicateFilter;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.filter.security.SecurityIdFilter;
import com.tradehero.th.fragments.security.SecurityItemView;
import com.tradehero.th.utils.DaggerUtils;
import java.util.List;
import javax.inject.Inject;

public class SecurityItemViewAdapter extends ArrayDTOAdapter<SecurityId, SecurityItemView>
{
    private final static String TAG = SecurityItemViewAdapter.class.getSimpleName();

    protected List<SecurityId> originalItems;
    protected Filter filterToUse;
    @Inject ListCharSequencePredicateFilter<SecurityId> securityIdPredicateFilter;

    //<editor-fold desc="Constructors">
    public SecurityItemViewAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
        DaggerUtils.inject(this);
        filterToUse = new SecurityItemFilter(securityIdPredicateFilter);
    }
    //</editor-fold>

    @Override public void setItems(List<SecurityId> items)
    {
        originalItems = items;
        setItemsToShow(securityIdPredicateFilter.filter(items));
    }

    protected void setItemsToShow(List<SecurityId> showItems)
    {
        THLog.d(TAG, "setItemsToShow " + (showItems == null ? "null" : showItems.size()));
        super.setItems(showItems);
    }

    @Override protected void fineTune(int position, SecurityId securityId, final SecurityItemView dtoView)
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

    @Override public Filter getFilter()
    {
        return filterToUse;
    }

    protected class SecurityItemFilter extends SecurityIdFilter
    {
        public SecurityItemFilter(ListCharSequencePredicateFilter<SecurityId> predicateFilter)
        {
            super(predicateFilter);
        }

        @Override protected FilterResults performFiltering(CharSequence charSequence)
        {

            return performFiltering(charSequence, originalItems);
        }

        @SuppressWarnings("unchecked")
        @Override protected void publishResults(CharSequence charSequence, SecurityFilterResults filterResults)
        {
            setItemsToShow(filterResults.castedValues);
            notifyDataSetChanged();
        }
    }
}