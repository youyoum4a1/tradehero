package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.app.AlertDialog;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.googleplay.BaseIABProductDetailsDecreasingPriceComparator;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.fragments.billing.THSKUDetailsAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 10:49 AM To change this template use File | Settings | File Templates. */
public class IABAlertDialogSKUUtil extends IABAlertDialogUtil
{
    public static final String TAG = IABAlertDialogSKUUtil.class.getSimpleName();

    @Inject THIABPurchaseCache thiabPurchaseCache;

    @Inject public IABAlertDialogSKUUtil()
    {
    }

    public AlertDialog popBuyDialog(
            Activity activity,
            THIABProductDetailDomainInformer domainInformer,
            IABAlertDialogUtil.OnDialogSKUDetailsClickListener<THIABProductDetail> clickListener,
            String skuDomain,
            int titleResId,
            Runnable runOnPurchaseComplete)
    {
        return popBuyDialog(activity, domainInformer, clickListener, skuDomain, titleResId, runOnPurchaseComplete, getEnabledItems());
    }

    protected AlertDialog popBuyDialog(
            Activity activity,
            THIABProductDetailDomainInformer domainInformer,
            IABAlertDialogUtil.OnDialogSKUDetailsClickListener<THIABProductDetail> clickListener,
            String skuDomain,
            int titleResId,
            Runnable runOnPurchaseComplete,
            Map<ProductIdentifier, Boolean> enabledItems)
    {
        final THSKUDetailsAdapter detailsAdapter = new THSKUDetailsAdapter(activity, activity.getLayoutInflater(), skuDomain);
        detailsAdapter.setProductDetailComparator(new THIABProductDetailComparator<>());
        detailsAdapter.setEnabledItems(enabledItems);
        detailsAdapter.setProductDetailComparator(
                new BaseIABProductDetailsDecreasingPriceComparator<THIABProductDetail>());
        List<THIABProductDetail> desiredSkuDetails = domainInformer.getDetailsOfDomain(skuDomain);
        detailsAdapter.setItems(desiredSkuDetails);

        return popBuyDialog(activity, detailsAdapter, titleResId, clickListener, runOnPurchaseComplete);
    }

    public HashMap<ProductIdentifier, Boolean> getEnabledItems()
    {
        HashMap<ProductIdentifier, Boolean> enabledItems = new HashMap<>();

        for (IABSKU key : thiabPurchaseCache.getKeys())
        {
            Timber.d("Disabling %s", key);
            enabledItems.put(key, false);
        }

        if (enabledItems.size() == 0)
        {
            enabledItems = null;
        }
        return enabledItems;
    }
}
