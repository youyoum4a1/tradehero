package com.tradehero.th.fragments.security;

import android.content.Context;
import android.widget.Filter;
import com.tradehero.common.widget.filter.ListCharSequencePredicateFilter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import javax.inject.Inject;

public class SimpleSecurityItemViewAdapter extends SecurityItemViewAdapter
{
    protected final Filter filterToUse;
    @Inject ListCharSequencePredicateFilter<SecurityCompactDTO> securityCompactPredicateFilter;

    //<editor-fold desc="Constructors">
    public SimpleSecurityItemViewAdapter(Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
        filterToUse = new SecurityItemFilter(securityCompactPredicateFilter);
    }
    //</editor-fold>

    @Override public ListCharSequencePredicateFilter<SecurityCompactDTO> getPredicateFilter()
    {
        return securityCompactPredicateFilter;
    }

    @Override public Filter getFilter()
    {
        return filterToUse;
    }
}