package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;

import com.android.internal.util.Predicate;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.utils.CollectionUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CountryAdapter extends ArrayDTOAdapterNew<CountryLanguagePairDTO, CountryItemView>
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