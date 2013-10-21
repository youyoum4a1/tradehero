package com.tradehero.th.widget.portfolio.header;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;

/**
 * Created by julien on 21/10/13
 * Interface for the header displayed on a PositionListFragment
 */
public interface PortfolioHeaderView
{
    public void bindOwnedPortfolioId(OwnedPortfolioId id);
}
