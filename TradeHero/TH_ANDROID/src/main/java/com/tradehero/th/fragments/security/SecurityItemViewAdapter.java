package com.tradehero.th.fragments.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import com.tradehero.common.widget.filter.ListCharSequencePredicateFilter;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.filter.security.SecurityCompactDTOFilter;
import com.tradehero.th.inject.HierarchyInjector;
import java.util.List;

abstract public class SecurityItemViewAdapter
        extends ArrayDTOAdapter<SecurityCompactDTO, SecurityItemView>
{
    public int itemHeight = 0;

    protected List<? extends SecurityCompactDTO> originalItems;

    //<editor-fold desc="Constructors">
    public SecurityItemViewAdapter(Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
        HierarchyInjector.inject(context, this);
    }
    //</editor-fold>

    @Override public void setItems(@NonNull List<SecurityCompactDTO> items)
    {
        originalItems = items;
        setItemsToShow(getPredicateFilter().filter(items));
    }

    protected void setItemsToShow(List<SecurityCompactDTO> showItems)
    {
        super.setItems(showItems);
    }

    abstract public ListCharSequencePredicateFilter<SecurityCompactDTO> getPredicateFilter();
    abstract public Filter getFilter();

    @Override protected void fineTune(int position, SecurityCompactDTO securityCompact, final SecurityItemView dtoView)
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

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        convertView = conditionalInflate(position, convertView, viewGroup);

        SecurityItemView dtoView = (SecurityItemView) convertView;
        SecurityCompactDTO dto = (SecurityCompactDTO) getItem(position);
        dtoView.display(dto);
        fineTune(position, dto, dtoView);
        return convertView;
    }

    protected class SecurityItemFilter
            extends SecurityCompactDTOFilter<SecurityCompactDTO>
    {
        public SecurityItemFilter(ListCharSequencePredicateFilter<SecurityCompactDTO> predicateFilter)
        {
            super(predicateFilter);
        }

        @Override protected Filter.FilterResults performFiltering(CharSequence charSequence)
        {

            return performFiltering(charSequence, originalItems);
        }

        @Override protected void publishResults(CharSequence charSequence, SecurityFilterResults<SecurityCompactDTO> filterResults)
        {
            setItemsToShow(filterResults.castedValues);
            notifyDataSetChanged();
        }
    }
}