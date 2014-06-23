package com.tradehero.th.fragments.security;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Filter;
import com.tradehero.common.widget.filter.ListCharSequencePredicateFilter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import javax.inject.Inject;

public class SimpleSecurityItemViewAdapter extends SecurityItemViewAdapter<SecurityCompactDTO>
{
    protected final Filter filterToUse;
    @Inject ListCharSequencePredicateFilter<SecurityCompactDTO> securityCompactPredicateFilter;

    //<editor-fold desc="Constructors">
    public SimpleSecurityItemViewAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
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