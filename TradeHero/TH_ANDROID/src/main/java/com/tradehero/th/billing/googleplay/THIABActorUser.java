package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.InventoryFetcher;
import com.tradehero.common.billing.googleplay.IABActorUser;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.billing.googleplay.exceptions.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABActorUser extends IABActorUser<
        IABSKU,
        THSKUDetails,
        InventoryFetcher.OnInventoryFetchedListener<IABSKU, THSKUDetails, IABException>,
        THIABPurchaseOrder,
        THIABOrderId,
        SKUPurchase,
        BillingPurchaser.OnPurchaseFinishedListener<
                IABSKU,
                THIABPurchaseOrder,
                THIABOrderId,
                SKUPurchase,
                IABException>,
        THIABActor,
        IABException>
{
}
