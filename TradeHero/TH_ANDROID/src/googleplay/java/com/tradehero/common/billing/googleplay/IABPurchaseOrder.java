package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.PurchaseOrder;

import android.support.annotation.NonNull;

public interface IABPurchaseOrder<IABSKUType extends IABSKU>
        extends PurchaseOrder<IABSKUType>
{
    @NonNull String getDeveloperPayload();
}
