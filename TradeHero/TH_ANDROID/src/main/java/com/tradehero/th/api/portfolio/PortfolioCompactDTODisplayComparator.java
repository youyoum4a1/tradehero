package com.ayondo.academy.api.portfolio;

import android.support.annotation.NonNull;
import java.io.Serializable;
import java.util.Comparator;

public class PortfolioCompactDTODisplayComparator implements Comparator<PortfolioCompactDTO>, Serializable
{
    @Override public int compare(@NonNull PortfolioCompactDTO lhs, @NonNull PortfolioCompactDTO rhs)
    {
        if (lhs.isDefault() && rhs.isDefault())
        {
            if (lhs.assetClass != null && rhs.assetClass != null)
            {
                return lhs.assetClass.compareTo(rhs.assetClass);
            }
            else if (lhs.assetClass != null)
            {
                return -1;
            }
            return 1;
        }
        else if (lhs.isDefault())
        {
            return -1;
        }
        else if (rhs.isDefault())
        {
            return 1;
        }
        else if (lhs.isWatchlist)
        {
            if (rhs.isWatchlist)
            {
                return 0;
            }
            return -1;
        }
        else if (rhs.isWatchlist)
        {
            return 1;
        }
        else if (lhs.providerId != null && rhs.providerId != null)
        {
            return lhs.providerId.compareTo(rhs.providerId);
        }
        else if (lhs.providerId != null)
        {
            return -1;
        }
        else if (rhs.providerId != null)
        {
            return 1;
        }
        return Integer.valueOf(lhs.id).compareTo(rhs.id);
    }
}
