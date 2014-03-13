package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.PurchaseReporter;

/**
 * Created by xavier on 3/13/14.
 */
public class THIABBillingRequest extends IABBillingRequest<
        IABSKU, THIABProductDetail,
        THIABPurchaseOrder, THIABOrderId,
        THIABPurchase, IABException>
{
    public static final String TAG = THIABBillingRequest.class.getSimpleName();

    protected THIABBillingRequest(BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException> inventoryFetchedListener,
            BillingPurchaseFetcher.OnPurchaseFetchedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> purchaseFetchedListener,
            BillingPurchaser.OnPurchaseFinishedListener<IABSKU, THIABPurchaseOrder, THIABOrderId, THIABPurchase, IABException> purchaseFinishedListener,
            PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> purchaseReportedListener,
            IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> consumptionFinishedListener)
    {
        super(inventoryFetchedListener, purchaseFetchedListener, purchaseFinishedListener, purchaseReportedListener, consumptionFinishedListener);
    }

    public static class THIABBuilder
            extends IABBuilder<
            IABSKU, THIABProductDetail,
            THIABPurchaseOrder, THIABOrderId,
            THIABPurchase, IABException>
    {
        public THIABBuilder()
        {
            super();
        }

        @Override public IABBillingRequest<IABSKU, THIABProductDetail, THIABPurchaseOrder, THIABOrderId, THIABPurchase, IABException> build()
        {
            return new THIABBillingRequest(
                    getInventoryFetchedListener(),
                    getPurchaseFetchedListener(),
                    getPurchaseFinishedListener(),
                    getPurchaseReportedListener(),
                    getConsumptionFinishedListener());
        }
    }
}
