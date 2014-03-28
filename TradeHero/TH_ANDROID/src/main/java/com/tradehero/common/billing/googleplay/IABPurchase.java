package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.persistence.DTO;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 5:28 PM To change this template use File | Settings | File Templates. */
public interface IABPurchase<
            IABSKUType extends IABSKU,
            IABOrderIdType extends IABOrderId>
        extends ProductPurchase<IABSKUType, IABOrderIdType>
{
    String getType();
    String getToken();
    String getOriginalJson();
    String getSignature();
    GooglePlayPurchaseDTO getGooglePlayPurchaseDTO();
}
