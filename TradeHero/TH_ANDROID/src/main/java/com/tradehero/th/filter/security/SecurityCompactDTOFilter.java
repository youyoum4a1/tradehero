package com.tradehero.th.filter.security;

import android.widget.Filter;
import com.tradehero.common.widget.filter.ListCharSequencePredicateFilter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.List;
import timber.log.Timber;

abstract public class SecurityCompactDTOFilter<SecurityCompactDTOType extends SecurityCompactDTO> extends Filter
{
    protected final ListCharSequencePredicateFilter<SecurityCompactDTOType> securityCompactDTOPredicateFilter;

    //<editor-fold desc="Constructors">
    public SecurityCompactDTOFilter(ListCharSequencePredicateFilter<SecurityCompactDTOType> predicateFilter)
    {
        super();
        this.securityCompactDTOPredicateFilter = predicateFilter;
    }
    //</editor-fold>

    protected FilterResults performFiltering(CharSequence charSequence, List<SecurityCompactDTOType> items)
    {
        securityCompactDTOPredicateFilter.setCharSequence(charSequence);
        SecurityFilterResults results = new SecurityFilterResults<SecurityCompactDTOType>();

        results.castedValues = securityCompactDTOPredicateFilter.filter(items);
        Timber.d("Count %d", results.castedValues == null ? null : results.castedValues.size());
        results.values = results.castedValues;
        results.count = results.castedValues == null ? 0 : results.castedValues.size();

        return results;
    }

    @SuppressWarnings("unchecked")
    @Override final protected void publishResults(CharSequence charSequence, FilterResults filterResults)
    {
        // TODO make a new instance instead of casting as it appears the FilterResults can be passed later,
        // for instance after a sleep.
        // https://www.crashlytics.com/tradehero/android/apps/com.tradehero.th/issues/539106b0e3de5099ba4db214
        publishResults(charSequence, (SecurityFilterResults<SecurityCompactDTOType>) filterResults);
    }

    abstract protected void publishResults(CharSequence charSequence, SecurityFilterResults<SecurityCompactDTOType> filterResults);

    protected class SecurityFilterResults<SecurityCompactDTOType extends SecurityCompactDTO> extends FilterResults
    {
        public List<SecurityCompactDTOType> castedValues;
    }
}
