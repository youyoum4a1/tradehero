package com.androidth.general.billing.samsung;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.tradehero.metrics.Analytics;
import com.androidth.general.R;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.billing.BaseBillingUtils;
import com.androidth.general.billing.THBillingAlertDialogRxUtil;
import com.androidth.general.billing.samsung.persistence.THSamsungPurchaseCacheRx;
import java.util.HashMap;
import javax.inject.Inject;
import timber.log.Timber;

public class THSamsungAlertDialogRxUtil extends THBillingAlertDialogRxUtil<
        SamsungSKU,
        THSamsungProductDetail,
        THSamsungLogicHolderRx,
        THSamsungOrderId,
        THSamsungPurchase>
{
    @NonNull protected final THSamsungPurchaseCacheRx thSamsungPurchaseCache;

    //<editor-fold desc="Constructors">
    @Inject public THSamsungAlertDialogRxUtil(
            @NonNull CurrentUserId currentUserId,
            @NonNull Analytics analytics,
            @NonNull THSamsungPurchaseCacheRx thSamsungPurchaseCache)
    {
        super(currentUserId, analytics);
        this.thSamsungPurchaseCache = thSamsungPurchaseCache;
    }
    //</editor-fold>

    //<editor-fold desc="SKU related">
    @Override @NonNull public HashMap<ProductIdentifier, Boolean> getEnabledItems()
    {
        HashMap<ProductIdentifier, Boolean> enabledItems = new HashMap<>();
        for (THSamsungPurchase value : thSamsungPurchaseCache.getValues())
        {
            Timber.d("Disabling %s", value);
            enabledItems.put(value.getProductIdentifier(), false);
        }
        return enabledItems;
    }
    //</editor-fold>

    public void sendSupportEmailRestoreFailed(final Context context, Exception exception)
    {
        context.startActivity(Intent.createChooser(
                BaseBillingUtils.getSupportPurchaseRestoreEmailIntent(context, currentUserId, exception),
                context.getString(R.string.iap_send_support_email_chooser_title)));
    }
}
