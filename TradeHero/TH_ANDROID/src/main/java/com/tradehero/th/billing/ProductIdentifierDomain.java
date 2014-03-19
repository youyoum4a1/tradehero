package com.tradehero.th.billing;

import com.tradehero.th.R;
import com.tradehero.th.utils.LocalyticsConstants;

/**
 * Created by xavier on 3/18/14.
 */
public enum ProductIdentifierDomain
{
    DOMAIN_VIRTUAL_DOLLAR(R.string.store_buy_virtual_dollar_window_title, LocalyticsConstants.BuyExtraCashDialog_Show),
    DOMAIN_FOLLOW_CREDITS(R.string.store_buy_follow_credits_window_message, LocalyticsConstants.BuyCreditsDialog_Show),
    DOMAIN_STOCK_ALERTS(R.string.store_buy_stock_alerts_window_title, LocalyticsConstants.BuyStockAlertDialog_Show),
    DOMAIN_RESET_PORTFOLIO(R.string.store_buy_reset_portfolio_window_title, LocalyticsConstants.ResetPortfolioDialog_Show);

    public final int storeTitleResId;
    public final String localyticsShowTag;

    ProductIdentifierDomain(int storeTitleResId, String localyticsShowTag)
    {
        this.storeTitleResId = storeTitleResId;
        this.localyticsShowTag = localyticsShowTag;
    }
}
