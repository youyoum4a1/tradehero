package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.PurchaseOrder;

public interface IABPurchaseOrder<IABSKUType extends IABSKU>
        extends PurchaseOrder<IABSKUType>
{
    String getDeveloperPayload();
    void setDeveloperPayload(String developerPayload);
}
