package com.tradehero.th.billing;

import com.tradehero.th.R;
import com.tradehero.th.utils.metrics.AnalyticsConstants;

public enum ProductIdentifierDomain
{
    DOMAIN_VIRTUAL_DOLLAR(R.string.store_buy_virtual_dollar_window_title, AnalyticsConstants.BuyExtraCashDialog_Show),
    DOMAIN_FOLLOW_CREDITS(R.string.store_buy_follow_credits_window_message, AnalyticsConstants.BuyCreditsDialog_Show),
    DOMAIN_STOCK_ALERTS(R.string.store_buy_stock_alerts_window_title, AnalyticsConstants.BuyStockAlertDialog_Show),
    DOMAIN_RESET_PORTFOLIO(R.string.store_buy_reset_portfolio_window_title, AnalyticsConstants.ResetPortfolioDialog_Show);

    public final int storeTitleResId;
    public final String localyticsShowTag;

    ProductIdentifierDomain(int storeTitleResId, String localyticsShowTag)
    {
        this.storeTitleResId = storeTitleResId;
        this.localyticsShowTag = localyticsShowTag;
    }
}
