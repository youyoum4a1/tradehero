package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.OnBillingAvailableListener;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.PurchaseReporter;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by xavier on 3/13/14.
 */
public class THIABBillingRequestFull extends THIABBillingRequest<
        IABSKU, THIABProductDetail,
        THIABPurchaseOrder, THIABOrderId,
        THIABPurchase, IABException>
{
    public static final String TAG = THIABBillingRequestFull.class.getSimpleName();

    protected THIABBillingRequestFull(
            OnBillingAvailableListener<IABException> billingAvailableListener,
            ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKU, IABException> productIdentifierFetchedListener,
            BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException> inventoryFetchedListener,
            BillingPurchaseFetcher.OnPurchaseFetchedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> purchaseFetchedListener,
            BillingPurchaser.OnPurchaseFinishedListener<IABSKU, THIABPurchaseOrder, THIABOrderId, THIABPurchase, IABException> purchaseFinishedListener,
            PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> purchaseReportedListener,
            IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> consumptionFinishedListener,
            Boolean billingAvailable,
            Boolean fetchProductIdentifiers,
            List<IABSKU> productIdentifiersForInventory,
            Boolean fetchPurchase,
            THIABPurchaseOrder purchaseOrder,
            THIABPurchase purchaseToReport,
            THIABPurchase purchaseToConsume)
    {
        super(billingAvailableListener, productIdentifierFetchedListener, inventoryFetchedListener, purchaseFetchedListener, purchaseFinishedListener,
                purchaseReportedListener, consumptionFinishedListener,
                billingAvailable, fetchProductIdentifiers, productIdentifiersForInventory, fetchPurchase, purchaseOrder, purchaseToReport, purchaseToConsume);
    }

    public static class THIABBuilder
            extends IABBuilder<
            IABSKU, THIABProductDetail,
            THIABPurchaseOrder, THIABOrderId,
            THIABPurchase, IABException>
    {
        @Inject public THIABBuilder()
        {
            super();
        }

        @Override public THIABBillingRequest<IABSKU, THIABProductDetail, THIABPurchaseOrder, THIABOrderId, THIABPurchase, IABException> build()
        {
            return new THIABBillingRequestFull(
                    getBillingAvailableListener(),
                    getProductIdentifierFetchedListener(),
                    getInventoryFetchedListener(),
                    getPurchaseFetchedListener(),
                    getPurchaseFinishedListener(),
                    getPurchaseReportedListener(),
                    getConsumptionFinishedListener(),
                    getBillingAvailable(),
                    getFetchProductIdentifiers(),
                    getProductIdentifiersForInventory(),
                    getFetchPurchase(),
                    getPurchaseOrder(),
                    getPurchaseToReport(),
                    getPurchaseToConsume());
        }
    }
}
