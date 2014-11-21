package com.tradehero.common.billing.googleplay;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.PurchaseOrder;

public interface IABPurchaseOrder<IABSKUType extends IABSKU>
        extends PurchaseOrder<IABSKUType>
{
    @NonNull String getDeveloperPayload();
}
