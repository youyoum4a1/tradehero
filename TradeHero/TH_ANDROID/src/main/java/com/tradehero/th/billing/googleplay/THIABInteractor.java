package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.IABInteractor;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABInteractor extends IABInteractor<
        IABSKU,
        THIABProductIdentifierFetcherHolder,
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                IABSKU,
                IABException>,
        THIABProductDetail,
        BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException>,
        THIABPurchaseOrder,
        THIABOrderId,
        THIABPurchase,
        BillingPurchaser.OnPurchaseFinishedListener<
                IABSKU,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase,
                IABException>,
        THIABLogicHolder,
        IABException>
{
}
