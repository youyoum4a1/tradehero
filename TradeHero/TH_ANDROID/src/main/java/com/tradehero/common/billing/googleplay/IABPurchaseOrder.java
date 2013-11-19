package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.PurchaseOrder;

/** Created with IntelliJ IDEA. User: xavier Date: 11/19/13 Time: 10:43 AM To change this template use File | Settings | File Templates. */
public interface IABPurchaseOrder<
        IABSKUType extends IABSKU>
            extends PurchaseOrder<IABSKUType>
{
    String getDeveloperPayload();
    void setDeveloperPayload(String developerPayload);
}
