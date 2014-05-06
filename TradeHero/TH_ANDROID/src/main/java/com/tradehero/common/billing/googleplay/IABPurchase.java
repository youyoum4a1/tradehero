package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.persistence.DTO;


public interface IABPurchase<
            IABSKUType extends IABSKU,
            IABOrderIdType extends IABOrderId>
        extends ProductPurchase<IABSKUType, IABOrderIdType>,
            DTO
{
    String getType();
    String getToken();
    String getOriginalJson();
    String getSignature();
    GooglePlayPurchaseDTO getGooglePlayPurchaseDTO();
}
