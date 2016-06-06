package com.androidth.general.common.billing.googleplay;

import com.androidth.general.common.billing.ProductPurchase;

public interface IABPurchase<
            IABSKUType extends IABSKU,
            IABOrderIdType extends IABOrderId>
        extends ProductPurchase<IABSKUType, IABOrderIdType>
{
    String getType();
    String getToken();
    String getOriginalJson();
    String getSignature();
}
