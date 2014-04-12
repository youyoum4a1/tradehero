package com.tradehero.th.fragments.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
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
    public int itemHeight = 0;

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

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        convertView = conditionalInflate(position, convertView, viewGroup);

        SecurityItemView dtoView = (SecurityItemView) convertView;
        SecurityCompactDTOType dto = (SecurityCompactDTOType) getItem(position);
        dtoView.display(dto);
        fineTune(position, dto, dtoView);
        if (itemHeight == 0 && convertView.getHeight() > 0)
        {
            itemHeight = convertView.getHeight();
            SharedPreferences pref = context.getSharedPreferences("trade_hero",
                    Context.MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("trending_item_height", itemHeight);
            editor.apply();
        }
        return convertView;
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