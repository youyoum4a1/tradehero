package com.tradehero.th.widget.portfolio;

import com.tradehero.th.api.portfolio.OwnedPortfolioId;

/** Created with IntelliJ IDEA. User: xavier Date: 10/25/13 Time: 4:19 PM To change this template use File | Settings | File Templates. */
public interface PortfolioRequestListener
{
    void onDefaultPortfolioRequested();
    void onPortfolioRequested(OwnedPortfolioId ownedPortfolioId);
}
