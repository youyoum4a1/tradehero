package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.PurchaseOrder;

import org.jetbrains.annotations.NotNull;

public interface IABPurchaseOrder<IABSKUType extends IABSKU>
        extends PurchaseOrder<IABSKUType>
{
    @NotNull String getDeveloperPayload();
}
