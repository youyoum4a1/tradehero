package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.app.AlertDialog;
import com.tradehero.common.billing.googleplay.BaseIABProductDetailsDecreasingPriceComparator;
import com.tradehero.th.fragments.billing.THSKUDetailsAdapter;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 10:49 AM To change this template use File | Settings | File Templates. */
@Singleton public class IABAlertDialogSKUUtil extends IABAlertDialogUtil
{
    public static final String TAG = IABAlertDialogSKUUtil.class.getSimpleName();

    @Inject public IABAlertDialogSKUUtil()
    {
    }

    public AlertDialog popBuyDialog(
            Activity activity,
            SKUDomainInformer domainInformer,
            IABAlertDialogUtil.OnDialogSKUDetailsClickListener<THIABProductDetail> clickListener,
            String skuDomain,
            int titleResId,
            Runnable runOnPurchaseComplete)
    {
        final THSKUDetailsAdapter detailsAdapter = new THSKUDetailsAdapter(activity, activity.getLayoutInflater(), skuDomain);
        detailsAdapter.setSkuDetailsComparator(new THIABProductDetailComparator<>());
        detailsAdapter.setSkuDetailsComparator(new BaseIABProductDetailsDecreasingPriceComparator<THIABProductDetail>());
        List<THIABProductDetail> desiredSkuDetails = domainInformer.getDetailsOfDomain(skuDomain);
        detailsAdapter.setItems(desiredSkuDetails);

        return popBuyDialog(activity, detailsAdapter, titleResId, clickListener, runOnPurchaseComplete);
    }
}
