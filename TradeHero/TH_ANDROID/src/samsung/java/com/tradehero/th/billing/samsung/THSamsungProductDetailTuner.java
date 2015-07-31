package com.tradehero.th.billing.samsung;

import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import timber.log.Timber;

public class THSamsungProductDetailTuner
{
    public static void fineTune(@NonNull THSamsungProductDetail productDetails)
    {
        switch (productDetails.getItemId())
        {
            case THSamsungConstants.EXTRA_CASH_T0_DATA_1:
                productDetails.iconResId = R.drawable.icn_th_dollars;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                break;
            case THSamsungConstants.EXTRA_CASH_T1_DATA_1:
                productDetails.iconResId = R.drawable.icn_th_dollars_50k;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                break;
            case THSamsungConstants.EXTRA_CASH_T2_DATA_1:
                productDetails.iconResId = R.drawable.icn_th_dollars_100k;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                break;

            case THSamsungConstants.RESET_PORTFOLIO_0_DATA_1:
                productDetails.iconResId = R.drawable.icn_reset_portfolio;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO;
                break;

            case THSamsungConstants.CREDIT_1_DATA_1:
                productDetails.iconResId = R.drawable.icn_follow_credits;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
                break;

            //case THSamsungConstants.CREDIT_5_DATA_1:
            //    productDetails.iconResId = R.drawable.credit_1;
            //    productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
            //    break;

            case THSamsungConstants.CREDIT_10_DATA_1:
                productDetails.iconResId = R.drawable.icn_follow_credits_10;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
                break;
            case THSamsungConstants.CREDIT_20_DATA_1:
                productDetails.iconResId = R.drawable.icn_follow_credits_20;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
                break;
            case THSamsungConstants.ALERT_1_DATA_1:
                productDetails.iconResId = R.drawable.buy_alerts_2;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_STOCK_ALERTS;
                break;
            case THSamsungConstants.ALERT_5_DATA_1:
                productDetails.iconResId = R.drawable.buy_alerts_5;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_STOCK_ALERTS;
                break;
            case THSamsungConstants.ALERT_UNLIMITED_DATA_1:
                productDetails.iconResId = R.drawable.buy_alerts_infinite;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_STOCK_ALERTS;
                break;

            default:
                Timber.e(new IllegalArgumentException(String.format("Unhandled productDetails key %s", productDetails.getItemId())), "");
        }
    }
}
