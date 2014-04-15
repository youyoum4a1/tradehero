package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.ProductDetailTuner;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import timber.log.Timber;

public class THSamsungProductDetailTuner implements ProductDetailTuner<SamsungSKU, THSamsungProductDetail>
{
    public THSamsungProductDetailTuner()
    {
        super();
    }

    @Override public void fineTune(THSamsungProductDetail productDetails)
    {
        if (productDetails.getProductIdentifier().groupId.equals(THSamsungConstants.IAP_ITEM_GROUP_ID))
        {
            switch (productDetails.getProductIdentifier().itemId)
            {
                case THSamsungConstants.EXTRA_CASH_T0_ITEM_ID:
                    productDetails.iconResId = R.drawable.cash_1;
                    productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                    // TODO price?
                    break;
                case THSamsungConstants.EXTRA_CASH_T1_ITEM_ID:
                    productDetails.iconResId = R.drawable.cash_2;
                    productDetails.hasRibbon = true;
                    productDetails.iconRibbonResId = R.drawable.ribbon_cash_disc10;
                    productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                    break;
                case THSamsungConstants.EXTRA_CASH_T2_ITEM_ID:
                    productDetails.iconResId = R.drawable.cash_3;
                    productDetails.hasRibbon = true;
                    productDetails.iconRibbonResId = R.drawable.ribbon_cash_best;
                    productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                    break;

                //case THSamsungConstants.RESET_PORTFOLIO_0:
                //    productDetails.iconResId = R.drawable.icn_reset_portfolio;
                //    productDetails.domain = ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO;
                //    break;

                //case THSamsungConstants.CREDIT_1:
                //    productDetails.iconResId = R.drawable.credit_1;
                //    productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
                //    break;

                //case THSamsungConstants.CREDIT_5:
                //    productDetails.iconResId = R.drawable.credit_1;
                //    productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
                //    break;

                //case THSamsungConstants.CREDIT_10:
                //    productDetails.iconResId = R.drawable.credit_5;
                //    productDetails.hasRibbon = true;
                //    productDetails.iconRibbonResId = R.drawable.ribbon_cash_disc5;
                //    productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
                //    break;

                //case THSamsungConstants.CREDIT_20:
                //    productDetails.iconResId = R.drawable.credit_10;
                //    productDetails.hasRibbon = true;
                //    productDetails.iconRibbonResId = R.drawable.ribbon_cc_best;
                //    productDetails.domain = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
                //    break;

                //case THSamsungConstants.ALERT_1:
                //    productDetails.iconResId = R.drawable.buy_alerts_2;
                //    productDetails.hasRibbon = false;
                //    productDetails.domain = ProductIdentifierDomain.DOMAIN_STOCK_ALERTS;
                //    break;
                //case THSamsungConstants.ALERT_5:
                //    productDetails.iconResId = R.drawable.buy_alerts_5;
                //    productDetails.hasRibbon = true;
                //    productDetails.iconRibbonResId = R.drawable.ribbon_cash_disc10;
                //    productDetails.domain = ProductIdentifierDomain.DOMAIN_STOCK_ALERTS;
                //    break;
                //case THSamsungConstants.ALERT_UNLIMITED:
                //    productDetails.iconResId = R.drawable.buy_alerts_infinite;
                //    productDetails.hasRibbon = true;
                //    productDetails.iconRibbonResId = R.drawable.ribbon_buy_alerts_best;
                //    productDetails.domain = ProductIdentifierDomain.DOMAIN_STOCK_ALERTS;
                //    break;

                default:
                    Timber.d("Unhandled productDetails key %s", productDetails.getProductIdentifier().itemId);
            }
        }
        else
        {
            Timber.e(new Exception(String.format("Unhandled groupId %s", productDetails.getProductIdentifier().groupId)), "");
        }
    }
}
