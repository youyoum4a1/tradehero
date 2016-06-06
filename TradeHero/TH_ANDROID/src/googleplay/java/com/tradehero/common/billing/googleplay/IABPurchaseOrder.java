package com.androidth.general.common.billing.googleplay;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.PurchaseOrder;

public interface IABPurchaseOrder<IABSKUType extends IABSKU>
        extends PurchaseOrder<IABSKUType>
{
    @NonNull String getDeveloperPayload();

    /**
     *
     * @return IABConstants.ITEM_TYPE_SUBS or IABConstants.ITEM_TYPE_INAPP
     */
    @NonNull String getType();
}
