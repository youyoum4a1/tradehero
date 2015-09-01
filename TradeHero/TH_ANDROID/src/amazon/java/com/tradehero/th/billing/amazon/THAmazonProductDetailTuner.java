package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import timber.log.Timber;

public class THAmazonProductDetailTuner
{
    public static void fineTune(@NonNull THAmazonProductDetail productDetails)
    {
        switch (productDetails.getProductIdentifier().skuId)
        {
            case THAmazonConstants.EXTRA_CASH_T0_KEY:
                productDetails.iconResId = R.drawable.icn_th_dollars;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                break;
            case THAmazonConstants.EXTRA_CASH_T1_KEY:
                productDetails.iconResId = R.drawable.icn_th_dollars_50k;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                break;
            case THAmazonConstants.EXTRA_CASH_T2_KEY:
                productDetails.iconResId = R.drawable.icn_th_dollars_100k;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                break;

            case THAmazonConstants.RESET_PORTFOLIO_0:
                productDetails.iconResId = R.drawable.icn_reset_portfolio;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO;
                break;

            case THAmazonConstants.ALERT_1:
                productDetails.iconResId = R.drawable.buy_alerts_2;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_STOCK_ALERTS;
                break;
            case THAmazonConstants.ALERT_5:
                productDetails.iconResId = R.drawable.buy_alerts_5;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_STOCK_ALERTS;
                break;
            case THAmazonConstants.ALERT_UNLIMITED:
                productDetails.iconResId = R.drawable.buy_alerts_infinite;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_STOCK_ALERTS;
                break;

            default:
                Timber.d("Unhandled productDetails key %s", productDetails.getProductIdentifier().skuId);
        }
    }
}
