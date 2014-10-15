package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.android.internal.util.Predicate;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOAdapterNew;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class CountryAdapter extends DTOAdapterNew<CountryLanguagePairDTO>
        implements Filterable
{
    private final Object lock = new Object();
    private List<CountryLanguagePairDTO> mOriginalValues;

    private Filter mFilter;

    public CountryAdapter(@NotNull Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }

    @Override public Filter getFilter()
    {
        if (mFilter == null)
        {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    @Override public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        return super.getView(position, convertView, parent);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.selected_country_item, parent, false);
        }
        TextView selectedView = (TextView) convertView;
        CountryLanguagePairDTO countryLanguagePairDTO = getItem(position);
        selectedView.setText(countryLanguagePairDTO.name);
        return convertView;
    }

    private class ArrayFilter extends Filter
    {
        @Override protected FilterResults performFiltering(CharSequence searchToken)
        {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null)
            {
                synchronized (lock)
                {
                    mOriginalValues = new ArrayList<>();
                    for (int i = 0; i < getCount(); i++)
                    {
                        mOriginalValues.add(getItem(i));
                    }
                }
            }

            if (searchToken == null || searchToken.length() == 0)
            {
                synchronized (lock)
                {
                    results.values = mOriginalValues;
                    results.count = mOriginalValues.size();
                }
            }
            else
            {
                final String searchString = searchToken.toString().toLowerCase();
                Collection<CountryLanguagePairDTO> newValues = CollectionUtils.filter(new ArrayList<>(mOriginalValues),
                        new SearchPredicate(searchString));

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override protected void publishResults(CharSequence constraint, FilterResults results)
        {
            if (results.count > 0)
            {
                synchronized (lock)
                {
                    clear();
                    //noinspection unchecked
                    addAll((Collection<CountryLanguagePairDTO>) results.values);
                }
                notifyDataSetChanged();
            }
            else
            {
                notifyDataSetInvalidated();
            }
        }

        private class SearchPredicate implements Predicate<CountryLanguagePairDTO>
        {
            private final String searchString;

            public SearchPredicate(String searchString)
            {
                this.searchString = searchString;
            }

            @Override public boolean apply(CountryLanguagePairDTO countryLanguagePairDTO)
            {
                return (countryLanguagePairDTO.name != null && countryLanguagePairDTO.name.toLowerCase().contains(searchString))
                        || String.format("%s-%s", countryLanguagePairDTO.languageCode, countryLanguagePairDTO.countryCode).contains(searchString);
            }
        }
    }
}