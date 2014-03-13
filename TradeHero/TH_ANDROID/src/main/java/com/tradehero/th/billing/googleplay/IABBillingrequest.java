package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABProductDetail;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseOrder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.THBillingRequest;

/**
 * Created by xavier on 3/13/14.
 */
public class IABBillingRequest<
        IABSKUType extends IABSKU,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABExceptionType extends IABException>
        extends THBillingRequest<
        IABSKUType,
        IABProductDetailType,
        IABPurchaseOrderType,
        IABOrderIdType,
        IABPurchaseType,
        IABExceptionType>
{
    public static final String TAG = IABBillingRequest.class.getSimpleName();

    private IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            IABExceptionType> consumptionFinishedListener;

    protected IABBillingRequest(
            BillingInventoryFetcher.OnInventoryFetchedListener<IABSKUType, IABProductDetailType, IABExceptionType> inventoryFetchedListener,
            BillingPurchaseFetcher.OnPurchaseFetchedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABExceptionType> purchaseFetchedListener,
            BillingPurchaser.OnPurchaseFinishedListener<IABSKUType, IABPurchaseOrderType, IABOrderIdType, IABPurchaseType, IABExceptionType> purchaseFinishedListener,
            PurchaseReporter.OnPurchaseReportedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABExceptionType> purchaseReportedListener,
            IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABExceptionType> consumptionFinishedListener)
    {
        super(inventoryFetchedListener, purchaseFetchedListener, purchaseFinishedListener, purchaseReportedListener);
        this.consumptionFinishedListener = consumptionFinishedListener;
    }

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
        private IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType> consumptionFinishedListener;

        public IABBuilder()
        {
            super();
        }

        @Override
        public IABBillingRequest<IABSKUType, IABProductDetailType, IABPurchaseOrderType, IABOrderIdType, IABPurchaseType,
                IABExceptionType> build()
        {
            return new IABBillingRequest<>(getInventoryFetchedListener(), getPurchaseFetchedListener(), getPurchaseFinishedListener(), getPurchaseReportedListener(), consumptionFinishedListener);
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
        //</editor-fold>
    }
}
