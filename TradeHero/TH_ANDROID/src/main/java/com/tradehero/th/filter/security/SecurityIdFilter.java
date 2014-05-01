package com.tradehero.th.filter.security;

import android.widget.Filter;
import com.tradehero.common.widget.filter.ListCharSequencePredicateFilter;
import com.tradehero.th.api.security.SecurityId;
import java.util.List;
import timber.log.Timber;

abstract public class SecurityIdFilter extends Filter
{
    protected ListCharSequencePredicateFilter<SecurityId> securityIdPatternFilter;

    public SecurityIdFilter(ListCharSequencePredicateFilter<SecurityId> predicateFilter)
    {
        super();
        this.securityIdPatternFilter = predicateFilter;
    }

    protected FilterResults performFiltering(CharSequence charSequence, List<SecurityId> items)
    {
        securityIdPatternFilter.setCharSequence(charSequence);
        SecurityFilterResults results = new SecurityFilterResults();

        results.castedValues = securityIdPatternFilter.filter(items);
        Timber.d("Count %d", results.castedValues == null ? null : results.castedValues.size());
        results.values = results.castedValues;
        results.count = results.castedValues == null ? 0 : results.castedValues.size();

        return results;
    }

    @Override final protected void publishResults(CharSequence charSequence,
            FilterResults filterResults)
    {
        publishResults(charSequence, (SecurityFilterResults) filterResults);
    }

    abstract protected void publishResults(CharSequence charSequence,
            SecurityFilterResults filterResults);

    protected class SecurityFilterResults extends FilterResults
    {
        public List<SecurityId> castedValues;
    }
}
