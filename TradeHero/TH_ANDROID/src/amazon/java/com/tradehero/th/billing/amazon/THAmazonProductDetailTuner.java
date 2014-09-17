package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THProductDetailTuner;
import javax.inject.Inject;
import timber.log.Timber;

public class THAmazonProductDetailTuner implements THProductDetailTuner<AmazonSKU, THAmazonProductDetail>
{
    //<editor-fold desc="Constructors">
    @Inject public THAmazonProductDetailTuner()
    {
        super();
    }
    //</editor-fold>

    @Override public void fineTune(THAmazonProductDetail productDetails)
    {
        switch (productDetails.getProductIdentifier().skuId)
        {
            case THAmazonConstants.EXTRA_CASH_T0_KEY:
                productDetails.iconResId = R.drawable.cash_1;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                break;
            case THAmazonConstants.EXTRA_CASH_T1_KEY:
                productDetails.iconResId = R.drawable.cash_2;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cash_disc10;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                break;
            case THAmazonConstants.EXTRA_CASH_T2_KEY:
                productDetails.iconResId = R.drawable.cash_3;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cash_best;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                break;

            case THAmazonConstants.RESET_PORTFOLIO_0:
                productDetails.iconResId = R.drawable.icn_reset_portfolio;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO;
                break;

            case THAmazonConstants.CREDIT_1:
                productDetails.iconResId = R.drawable.credit_1;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
                break;

            //case THAmazonConstants.CREDIT_5:
            //    productDetails.iconResId = R.drawable.credit_1;
            //    productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
            //    break;

            case THAmazonConstants.CREDIT_10:
                productDetails.iconResId = R.drawable.credit_5;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cash_disc5;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
                break;

            case THAmazonConstants.CREDIT_20:
                productDetails.iconResId = R.drawable.credit_10;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cc_best;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
                break;

            case THAmazonConstants.ALERT_1:
                productDetails.iconResId = R.drawable.buy_alerts_2;
                productDetails.hasRibbon = false;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_STOCK_ALERTS;
                break;
            case THAmazonConstants.ALERT_5:
                productDetails.iconResId = R.drawable.buy_alerts_5;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cash_disc10;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_STOCK_ALERTS;
                break;
            case THAmazonConstants.ALERT_UNLIMITED:
                productDetails.iconResId = R.drawable.buy_alerts_infinite;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_buy_alerts_best;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_STOCK_ALERTS;
                break;

            default:
                Timber.d("Unhandled productDetails key %s", productDetails.getProductIdentifier().skuId);
        }
    }
}
