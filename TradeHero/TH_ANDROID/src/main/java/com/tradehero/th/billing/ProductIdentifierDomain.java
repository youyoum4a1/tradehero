package com.tradehero.th.billing;

import com.tradehero.th.utils.LocalyticsConstants;

/**
 * Created by xavier on 3/18/14.
 */
public enum ProductIdentifierDomain
{
    DOMAIN_VIRTUAL_DOLLAR(LocalyticsConstants.BuyExtraCashDialog_Show),
    DOMAIN_FOLLOW_CREDITS(LocalyticsConstants.BuyCreditsDialog_Show),
    DOMAIN_STOCK_ALERTS(LocalyticsConstants.BuyStockAlertDialog_Show),
    DOMAIN_RESET_PORTFOLIO(LocalyticsConstants.ResetPortfolioDialog_Show);

    public final String localyticsShowTag;

    ProductIdentifierDomain(String localyticsShowTag)
    {
        this.localyticsShowTag = localyticsShowTag;
    }
}
