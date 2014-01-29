package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.ProductDetailTuner;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 3:25 PM To change this template use File | Settings | File Templates. */
public class THSKUDetailTuner implements ProductDetailTuner<IABSKU, THIABProductDetail>
{
    public static final String TAG = THSKUDetailTuner.class.getSimpleName();

    public THSKUDetailTuner()
    {
        super();
    }

    @Override public void fineTune(THIABProductDetail productDetails)
    {
        switch (productDetails.getProductIdentifier().identifier)
        {
            case THIABSKUFetcher.EXTRA_CASH_T0_KEY:
                productDetails.iconResId = R.drawable.cash_1;
                productDetails.domain = THIABProductDetail.DOMAIN_VIRTUAL_DOLLAR;
                break;
            case THIABSKUFetcher.EXTRA_CASH_T1_KEY:
                productDetails.iconResId = R.drawable.cash_2;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cash_disc10;
                productDetails.domain = THIABProductDetail.DOMAIN_VIRTUAL_DOLLAR;
                break;
            case THIABSKUFetcher.EXTRA_CASH_T2_KEY:
                productDetails.iconResId = R.drawable.cash_3;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cash_best;
                productDetails.domain = THIABProductDetail.DOMAIN_VIRTUAL_DOLLAR;
                break;

            case THIABSKUFetcher.RESET_PORTFOLIO_0:
                productDetails.iconResId = R.drawable.icn_reset_portfolio;
                productDetails.domain = THIABProductDetail.DOMAIN_RESET_PORTFOLIO;
                break;

            case THIABSKUFetcher.CREDIT_1:
                productDetails.iconResId = R.drawable.credit_1;
                productDetails.domain = THIABProductDetail.DOMAIN_FOLLOW_CREDITS;
                break;

            //case THIABSKUFetcher.CREDIT_5:
            //    productDetails.iconResId = R.drawable.credit_1;
            //    productDetails.domain = THIABProductDetail.DOMAIN_FOLLOW_CREDITS;
            //    break;

            case THIABSKUFetcher.CREDIT_10:
                productDetails.iconResId = R.drawable.credit_5;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cash_disc5;
                productDetails.domain = THIABProductDetail.DOMAIN_FOLLOW_CREDITS;
                break;

            case THIABSKUFetcher.CREDIT_20:
                productDetails.iconResId = R.drawable.credit_10;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cc_best;
                productDetails.domain = THIABProductDetail.DOMAIN_FOLLOW_CREDITS;
                break;

            case THIABSKUFetcher.ALERT_1:
                productDetails.iconResId = R.drawable.buy_alerts_2;
                productDetails.hasRibbon = false;
                productDetails.domain = THIABProductDetail.DOMAIN_STOCK_ALERTS;
                break;
            case THIABSKUFetcher.ALERT_5:
                productDetails.iconResId = R.drawable.buy_alerts_5;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cash_disc10;
                productDetails.domain = THIABProductDetail.DOMAIN_STOCK_ALERTS;
                break;
            case THIABSKUFetcher.ALERT_UNLIMITED:
                productDetails.iconResId = R.drawable.buy_alerts_infinite;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_buy_alerts_best;
                productDetails.domain = THIABProductDetail.DOMAIN_STOCK_ALERTS;
                break;

            default:
                THLog.d(TAG, "Unhandled productDetails key " + productDetails.getProductIdentifier().identifier);
        }
    }
}
