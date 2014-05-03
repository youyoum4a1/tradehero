package com.tradehero.th.api.portfolio;

import java.util.Comparator;


public class PortfolioCompactDTODisplayComparator implements Comparator<PortfolioCompactDTO>
{
    public static final String TAG = PortfolioCompactDTODisplayComparator.class.getSimpleName();

    public PortfolioCompactDTODisplayComparator()
    {
    }

    @Override public int compare(PortfolioCompactDTO lhs, PortfolioCompactDTO rhs)
    {
        if (lhs == null)
        {
            return rhs == null ? 0 : 1;
        }
        else if (rhs == null)
        {
            return -1;
        }
        else if (lhs.isDefault())
        {
            return rhs.isDefault() ? Integer.valueOf(lhs.id).compareTo(rhs.id) : -1;
        }
        else if (rhs.isDefault())
        {
            return 1;
        }
        else if (lhs.providerId != null && !lhs.isWatchlist)
        {
            if (rhs.providerId != null && !rhs.isWatchlist)
            {
                int providerIdComp = lhs.providerId.compareTo(rhs.providerId);
                return providerIdComp != 0 ? providerIdComp : Integer.valueOf(lhs.id).compareTo(rhs.id);
            }
            return -1;
        }
        else if (rhs.providerId != null && !rhs.isWatchlist)
        {
            return 1;
        }
        else if (lhs.providerId == null && lhs.isWatchlist)
        {
            return rhs.providerId == null && rhs.isWatchlist ? Integer.valueOf(lhs.id).compareTo(rhs.id) : -1;
        }
        else if (rhs.providerId == null && rhs.isWatchlist)
        {
            return 1;
        }
        else if (lhs.providerId != null)
        {
            if (rhs.providerId != null)
            {
                int providerIdComp = lhs.providerId.compareTo(rhs.providerId);
                return providerIdComp != 0 ? providerIdComp : Integer.valueOf(lhs.id).compareTo(rhs.id);
            }
            return -1;
        }
        else if (rhs.providerId != null)
        {
            return 1;
        }
        return Integer.valueOf(lhs.id).compareTo(rhs.id);
    }
}
