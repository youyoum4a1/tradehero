package com.ayondo.academy.fragments.security;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Filter;
import com.tradehero.common.widget.filter.ListCharSequencePredicateFilter;
import com.ayondo.academy.api.quote.QuoteDTO;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.api.security.SecurityIntegerId;
import com.ayondo.academy.api.security.compact.FxSecurityCompactDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void updatePrices(@NonNull Context context, @NonNull List<? extends QuoteDTO> quotes)
    {
        Map<SecurityIntegerId, QuoteDTO> map = new HashMap<>();
        for (QuoteDTO quote : quotes)
        {
            map.put(quote.getSecurityIntegerId(), quote);
        }
        updatePrices(map);
    }

    public void updatePrices(@NonNull Map<SecurityIntegerId, ? extends QuoteDTO> quotes)
    {
        SecurityCompactDTO securityCompactDTO;
        QuoteDTO quote;
        if (getCount() > 0)
        {
            for (int index = 0; index < getCount(); index++)
            {
                securityCompactDTO = (SecurityCompactDTO) getItem(index);
                quote = quotes.get(securityCompactDTO.getSecurityIntegerId());
                if (quote != null)
                {
                    if (securityCompactDTO instanceof FxSecurityCompactDTO)
                    {
                        ((FxSecurityCompactDTO) securityCompactDTO).setAskPrice(quote.ask);
                        ((FxSecurityCompactDTO) securityCompactDTO).setBidPrice(quote.bid);
                    }
                    else
                    {
                        securityCompactDTO.askPrice = quote.ask;
                        securityCompactDTO.bidPrice = quote.bid;
                    }
                }
            }
        }
    }
}