package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.googleplay.IABLogicHolder;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABLogicHolder extends
        THIABProductDetailDomainInformer,
        THIABPurchaseReporterHolder, // This is redundant but allows passing of interface
        THIABPurchaseConsumerHolder, // This is redundant but allows passing of interface
        IABLogicHolder<
                        IABSKU,
                        THIABProductDetail,
                        THIABPurchaseOrder,
                        THIABOrderId,
                        THIABPurchase,
                        BillingPurchaseFetcher.OnPurchaseFetchedListener<IABSKU, THIABOrderId, THIABPurchase, IABException>,
                        BillingPurchaser.OnPurchaseFinishedListener<IABSKU, THIABPurchaseOrder, THIABOrderId, THIABPurchase, IABException>,
                        IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException>,
                        IABException>
{
    @Deprecated
    THIABInventoryFetcherHolder getInventoryFetcherHolder();
    @Deprecated
    THIABPurchaseFetcherHolder getPurchaseFetcherHolder();
    @Deprecated
    THIABPurchaserHolder getPurchaserHolder();
}
