package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.InventoryFetcher;
import com.tradehero.common.billing.googleplay.BaseIABPurchase;
import com.tradehero.common.billing.googleplay.IABActor;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exceptions.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABActor extends
        SKUDomainInformer,
        THIABActorSKUFetcher, // This is redundant but allows passing of interface
        THIABActorInventoryFetcher, // This is redundant but allows passing of interface
        THIABActorPurchaseFetcher, // This is redundant but allows passing of interface
        THIABActorPurchaser, // This is redundant but allows passing of interface
        THIABActorPurchaseReporter, // This is redundant but allows passing of interface
        THIABActorPurchaseConsumer, // This is redundant but allows passing of interface
        IABActor<
                IABSKU,
                THIABProductDetail,
                InventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException>,
                THIABPurchaseOrder,
                THIABOrderId,
                BaseIABPurchase,
                IABPurchaseFetcher.OnPurchaseFetchedListener<IABSKU, THIABOrderId, BaseIABPurchase>,
                BillingPurchaser.OnPurchaseFinishedListener<IABSKU, THIABPurchaseOrder, THIABOrderId, BaseIABPurchase, IABException>,
                IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, BaseIABPurchase, IABException>,
                IABException>
{
}
