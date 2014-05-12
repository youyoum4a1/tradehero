package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.ProductDetailTuner;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import timber.log.Timber;

public class THIABProductDetailTuner implements ProductDetailTuner<IABSKU, THIABProductDetail>
{
    public THIABProductDetailTuner()
    {
        super();
    }

    @Override public void fineTune(THIABProductDetail productDetails)
    {
        switch (productDetails.getProductIdentifier().identifier)
        {
            case THIABConstants.EXTRA_CASH_T0_KEY:
                productDetails.iconResId = R.drawable.cash_1;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                break;
            case THIABConstants.EXTRA_CASH_T1_KEY:
                productDetails.iconResId = R.drawable.cash_2;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cash_disc10;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                break;
            case THIABConstants.EXTRA_CASH_T2_KEY:
                productDetails.iconResId = R.drawable.cash_3;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cash_best;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                break;

            case THIABConstants.RESET_PORTFOLIO_0:
                productDetails.iconResId = R.drawable.icn_reset_portfolio;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO;
                break;

            case THIABConstants.CREDIT_1:
                productDetails.iconResId = R.drawable.credit_1;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
                break;

            //case THIABConstants.CREDIT_5:
            //    productDetails.iconResId = R.drawable.credit_1;
            //    productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
            //    break;

            case THIABConstants.CREDIT_10:
                productDetails.iconResId = R.drawable.credit_5;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cash_disc5;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
                break;

            case THIABConstants.CREDIT_20:
                productDetails.iconResId = R.drawable.credit_10;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cc_best;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
                break;

            case THIABConstants.ALERT_1:
                productDetails.iconResId = R.drawable.buy_alerts_2;
                productDetails.hasRibbon = false;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_STOCK_ALERTS;
                break;
            case THIABConstants.ALERT_5:
                productDetails.iconResId = R.drawable.buy_alerts_5;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cash_disc10;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_STOCK_ALERTS;
                break;
            case THIABConstants.ALERT_UNLIMITED:
                productDetails.iconResId = R.drawable.buy_alerts_infinite;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_buy_alerts_best;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_STOCK_ALERTS;
                break;

            default:
                Timber.d("Unhandled productDetails key %s", productDetails.getProductIdentifier().identifier);
        }
    }
}
