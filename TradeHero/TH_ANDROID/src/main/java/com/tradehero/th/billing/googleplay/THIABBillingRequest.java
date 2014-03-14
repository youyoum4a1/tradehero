package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.OnBillingAvailableListener;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABProductDetail;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseOrder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.THBillingRequest;
import java.util.List;

/**
 * Created by xavier on 3/13/14.
 */
public class THIABBillingRequest<
        IABSKUType extends IABSKU,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABExceptionType extends IABException>
        extends
        THBillingRequest<
                IABSKUType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>
{
    public static final String TAG = THIABBillingRequest.class.getSimpleName();

    //<editor-fold desc="Listeners">
    private IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            IABExceptionType> consumptionFinishedListener;
    //</editor-fold>

    private IABPurchaseType purchaseToConsume;

    protected THIABBillingRequest(
            OnBillingAvailableListener<IABExceptionType> billingAvailableListener,
            ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKUType, IABExceptionType> productIdentifierFetchedListener,
            BillingInventoryFetcher.OnInventoryFetchedListener<IABSKUType, IABProductDetailType, IABExceptionType> inventoryFetchedListener,
            BillingPurchaseFetcher.OnPurchaseFetchedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABExceptionType> purchaseFetchedListener,
            BillingPurchaser.OnPurchaseFinishedListener<IABSKUType, IABPurchaseOrderType, IABOrderIdType, IABPurchaseType, IABExceptionType> purchaseFinishedListener,
            PurchaseReporter.OnPurchaseReportedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABExceptionType> purchaseReportedListener,
            IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABExceptionType> consumptionFinishedListener,
            Boolean billingAvailable,
            Boolean fetchProductIdentifiers,
            List<IABSKUType> productIdentifiersForInventory,
            Boolean fetchPurchase,
            IABPurchaseOrderType purchaseOrder,
            IABPurchaseType purchaseToReport,
            IABPurchaseType purchaseToConsume)
    {
        super(billingAvailableListener, productIdentifierFetchedListener,
                inventoryFetchedListener, purchaseFetchedListener, purchaseFinishedListener, purchaseReportedListener,
                billingAvailable, fetchProductIdentifiers, productIdentifiersForInventory, fetchPurchase, purchaseOrder, purchaseToReport);
        this.consumptionFinishedListener = consumptionFinishedListener;
        this.purchaseToConsume = purchaseToConsume;
    }

    //<editor-fold desc="Accessors">
    public IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABExceptionType> getConsumptionFinishedListener()
    {
        return consumptionFinishedListener;
    }

    public void setConsumptionFinishedListener(
            IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABExceptionType> consumptionFinishedListener)
    {
        this.consumptionFinishedListener = consumptionFinishedListener;
    }

    public IABPurchaseType getPurchaseToConsume()
    {
        return purchaseToConsume;
    }
    //</editor-fold>

    public static class IABBuilder<
            IABSKUType extends IABSKU,
            IABProductDetailType extends IABProductDetail<IABSKUType>,
            IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
            IABOrderIdType extends IABOrderId,
            IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
            IABExceptionType extends IABException>
            extends THBuilder<
            IABSKUType,
            IABProductDetailType,
            IABPurchaseOrderType,
            IABOrderIdType,
            IABPurchaseType,
            IABExceptionType>
    {
        //<editor-fold desc="Listeners">
        private IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType> consumptionFinishedListener;
        //</editor-fold>

        private IABPurchaseType purchaseToConsume;

        public IABBuilder()
        {
            super();
        }

        @Override
        public THIABBillingRequest<IABSKUType, IABProductDetailType, IABPurchaseOrderType, IABOrderIdType, IABPurchaseType,
                                        IABExceptionType> build()
        {
            return new THIABBillingRequest<>(
                    getBillingAvailableListener(),
                    getProductIdentifierFetchedListener(),
                    getInventoryFetchedListener(),
                    getPurchaseFetchedListener(),
                    getPurchaseFinishedListener(),
                    getPurchaseReportedListener(),
                    consumptionFinishedListener,
                    getBillingAvailable(),
                    getFetchProductIdentifiers(),
                    getProductIdentifiersForInventory(),
                    getFetchPurchase(),
                    getPurchaseOrder(),
                    getPurchaseToReport(),
                    purchaseToConsume);
        }

        @Override protected List<Object> getTests()
        {
            List<Object> tests = super.getTests();
            tests.add(purchaseToConsume);
            return tests;
        }

        //<editor-fold desc="Accessors">
        public IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABExceptionType> getConsumptionFinishedListener()
        {
            return consumptionFinishedListener;
        }

        public void setConsumptionFinishedListener(
                IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABExceptionType> consumptionFinishedListener)
        {
            this.consumptionFinishedListener = consumptionFinishedListener;
        }

        public IABPurchaseType getPurchaseToConsume()
        {
            return purchaseToConsume;
        }

        public void setPurchaseToConsume(IABPurchaseType purchaseToConsume)
        {
            this.purchaseToConsume = purchaseToConsume;
        }
        //</editor-fold>
    }
}
