package com.tradehero.th.billing.googleplay;

import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import timber.log.Timber;

public class THIABProductDetailTuner
{
    public static void fineTune(@NonNull THIABProductDetail productDetails)
    {
        switch (productDetails.getProductIdentifier().identifier)
        {
            case THIABConstants.EXTRA_CASH_T0_KEY:
                productDetails.iconResId = R.drawable.icn_th_dollars;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                productDetails.displayOrder = 0;
                productDetails.storeTitleResId = R.string.store_virtual_dollars_10k_title;
                productDetails.storeDescriptionResId = R.string.store_virtual_dollars_10k_description;
                break;
            case THIABConstants.EXTRA_CASH_T1_KEY:
                productDetails.iconResId = R.drawable.icn_th_dollars_50k;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                productDetails.displayOrder = 1;
                productDetails.storeTitleResId = R.string.store_virtual_dollars_50k_title;
                productDetails.storeDescriptionResId = R.string.store_virtual_dollars_50k_description;
                break;
            case THIABConstants.EXTRA_CASH_T2_KEY:
                productDetails.iconResId = R.drawable.icn_th_dollars_100k;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR;
                productDetails.displayOrder = 2;
                productDetails.storeTitleResId = R.string.store_virtual_dollars_100k_title;
                productDetails.storeDescriptionResId = R.string.store_virtual_dollars_100k_description;
                break;

            case THIABConstants.RESET_PORTFOLIO_0:
                productDetails.iconResId = R.drawable.icn_reset_portfolio;
                productDetails.domain = ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO;
                productDetails.displayOrder = 3;
                productDetails.storeTitleResId = R.string.store_buy_reset_portfolio_window_title;
                productDetails.storeDescriptionResId = R.string.store_buy_reset_portfolio_window_message;
                break;
            default:
                Timber.d("Unhandled productDetails key %s", productDetails.getProductIdentifier().identifier);
        }
    }
}
