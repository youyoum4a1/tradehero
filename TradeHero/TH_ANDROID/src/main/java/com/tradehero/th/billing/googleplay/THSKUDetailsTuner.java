package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.ProductDetailsTuner;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 3:25 PM To change this template use File | Settings | File Templates. */
public class THSKUDetailsTuner implements ProductDetailsTuner<THSKUDetails>
{
    public static final String TAG = THSKUDetailsTuner.class.getSimpleName();

    public THSKUDetailsTuner()
    {
        super();
    }

    @Override public void fineTune(THSKUDetails productDetails)
    {
        switch (productDetails.getProductIdentifier().identifier)
        {
            case SKUFetcher.EXTRA_CASH_T0_KEY:
                productDetails.iconResId = R.drawable.cash_1;
                productDetails.domain = THSKUDetails.DOMAIN_VIRTUAL_DOLLAR;
                break;
            case SKUFetcher.EXTRA_CASH_T1_KEY:
                productDetails.iconResId = R.drawable.cash_2;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cash_disc10;
                productDetails.domain = THSKUDetails.DOMAIN_VIRTUAL_DOLLAR;
                break;
            case SKUFetcher.EXTRA_CASH_T2_KEY:
                productDetails.iconResId = R.drawable.cash_3;
                productDetails.hasRibbon = true;
                productDetails.iconRibbonResId = R.drawable.ribbon_cash_best;
                productDetails.domain = THSKUDetails.DOMAIN_VIRTUAL_DOLLAR;
                break;
            default:
                THLog.d(TAG, "Unhandled productDetails key " + productDetails.getProductIdentifier().identifier);
        }
    }
}
