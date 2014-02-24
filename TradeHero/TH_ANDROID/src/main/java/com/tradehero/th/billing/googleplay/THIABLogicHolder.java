package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.IABLogicHolder;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABLogicHolder extends
        THIABProductDetailDomainInformer,
        THIABPurchaseFetcherHolder, // This is redundant but allows passing of interface
        THIABPurchaseReporterHolder, // This is redundant but allows passing of interface
        THIABPurchaseConsumerHolder, // This is redundant but allows passing of interface
        IABLogicHolder<
                        IABSKU,
                        THIABProductDetail,
                        THIABPurchaseOrder,
                        THIABOrderId,
                        THIABPurchase,
                        IABPurchaseFetcher.OnPurchaseFetchedListener<IABSKU, THIABOrderId, THIABPurchase>,
                        BillingPurchaser.OnPurchaseFinishedListener<IABSKU, THIABPurchaseOrder, THIABOrderId, THIABPurchase, IABException>,
                        IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException>,
                        IABException>
{
    @Deprecated
    THIABInventoryFetcherHolder getInventoryFetcherHolder();
    @Deprecated
    THIABPurchaserHolder getPurchaserHolder();
}
