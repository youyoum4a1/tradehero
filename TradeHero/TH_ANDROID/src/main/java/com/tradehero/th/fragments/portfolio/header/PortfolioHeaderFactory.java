package com.tradehero.th.fragments.portfolio.header;

import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Singleton Class creating instances of PortfolioHeaderView based on which arguments are passed to the PositionListFragment
 */
@Singleton public class PortfolioHeaderFactory
{
    @Inject protected CurrentUserId currentUserId;

    public int layoutIdFor(Bundle ownedPortfolioIdBundle)
    {
        if (ownedPortfolioIdBundle == null)
        {
            throw new PortfolioHeaderFactoryException("Unable to build arguments from Bundle");
        }
        return layoutIdFor(new OwnedPortfolioId(ownedPortfolioIdBundle));
    }

    public int layoutIdFor(OwnedPortfolioId ownedPortfolioId)
    {
        return layoutIdFor(ownedPortfolioId.getUserBaseKey());
    }

    public int layoutIdFor(UserBaseKey userBaseKey)
    {
        if (userBaseKey.equals(currentUserId.toUserBaseKey()))
        {
            return R.layout.portfolio_header_current_user_view;
        }
        else
        {
            return R.layout.portfolio_header_other_user_view;
        }
    }
}
