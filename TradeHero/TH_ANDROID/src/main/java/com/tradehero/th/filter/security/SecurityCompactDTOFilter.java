package com.tradehero.th.filter.security;

import android.widget.Filter;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.widget.filter.ListCharSequencePredicateFilter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.List;

/**
 * Created by xavier on 1/24/14.
 */
abstract public class SecurityCompactDTOFilter<SecurityCompactDTOType extends SecurityCompactDTO> extends Filter
{
    public static final String TAG = SecurityCompactDTOFilter.class.getSimpleName();

    protected ListCharSequencePredicateFilter<SecurityCompactDTOType> securityCompactDTOPredicateFilter;

    public SecurityCompactDTOFilter(ListCharSequencePredicateFilter<SecurityCompactDTOType> predicateFilter)
    {
        super();
        this.securityCompactDTOPredicateFilter = predicateFilter;
    }

    protected FilterResults performFiltering(CharSequence charSequence, List<SecurityCompactDTOType> items)
    {
        securityCompactDTOPredicateFilter.setCharSequence(charSequence);
        SecurityFilterResults results = new SecurityFilterResults<SecurityCompactDTOType>();

        results.castedValues = securityCompactDTOPredicateFilter.filter(items);
        THLog.d(TAG, "Count " + (results.castedValues == null ? null : results.castedValues.size()));
        results.values = results.castedValues;
        results.count = results.castedValues == null ? 0 : results.castedValues.size();

        return results;
    }

    @SuppressWarnings("unchecked")
    @Override final protected void publishResults(CharSequence charSequence, FilterResults filterResults)
    {
        publishResults(charSequence, (SecurityFilterResults<SecurityCompactDTOType>) filterResults);
    }

    abstract protected void publishResults(CharSequence charSequence, SecurityFilterResults<SecurityCompactDTOType> filterResults);

    protected class SecurityFilterResults<SecurityCompactDTOType extends SecurityCompactDTO> extends FilterResults
    {
        public List<SecurityCompactDTOType> castedValues;
    }
}
