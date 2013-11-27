package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.InventoryFetcher;
import com.tradehero.common.billing.googleplay.IABActor;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.BasePurchaseReporter;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABActor extends
        SKUDomainInformer,
        THIABActorInventoryFetcher, // This is redundant but allows passing of interface
        THIABActorPurchaseFetcher, // This is redundant but allows passing of interface
        THIABActorPurchaseReporter, // This is redundant but allows passing of interface
        THIABActorPurchaseConsumer, // This is redundant but allows passing of interface
        IABActor<
                IABSKU,
                THSKUDetails,
                InventoryFetcher.OnInventoryFetchedListener<IABSKU, THSKUDetails, IABException>,
                THIABPurchaseOrder,
                THIABOrderId,
                SKUPurchase,
                IABPurchaseFetcher.OnPurchaseFetchedListener<IABSKU, THIABOrderId, SKUPurchase>,
                BillingPurchaser.OnPurchaseFinishedListener<IABSKU, THIABPurchaseOrder, THIABOrderId, SKUPurchase, IABException>,
                IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, SKUPurchase, IABException>,
                IABException>
{
    IABSKUFetcher.OnSKUFetchedListener<IABSKU> getSkuFetchedListener(int requestCode);
    int registerSkuFetchedListener(IABSKUFetcher.OnSKUFetchedListener<IABSKU> skuFetchedListener);
    void launchSkuFetchSequence(int requestCode);

    List<THSKUDetails> getDetailsOfDomain(String domain);
}
