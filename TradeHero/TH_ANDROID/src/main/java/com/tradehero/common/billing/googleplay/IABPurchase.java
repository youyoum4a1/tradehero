package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductPurchase;


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
