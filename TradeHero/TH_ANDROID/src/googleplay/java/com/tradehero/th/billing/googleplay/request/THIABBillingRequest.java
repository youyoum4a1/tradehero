package com.tradehero.th.billing.googleplay.request;

import com.tradehero.common.billing.googleplay.BaseIABSKUList;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABProductDetail;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseOrder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.THProductPurchase;
import com.tradehero.th.billing.THPurchaseOrder;
import com.tradehero.th.billing.request.THBillingRequest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class THIABBillingRequest<
        IABSKUListKeyType extends IABSKUListKey,
        IABSKUType extends IABSKU,
        IABSKUListType extends BaseIABSKUList<IABSKUType>,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>
                & THPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>
                & THProductPurchase<IABSKUType, IABOrderIdType>,
        IABExceptionType extends IABException>
        extends
        THBillingRequest<
                IABSKUListKeyType,
                IABSKUType,
                IABSKUListType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>
{
    public boolean consumePurchase;
    public IABPurchaseType purchaseToConsume;
    @Nullable public IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            IABExceptionType> consumptionFinishedListener;

    //<editor-fold desc="Constructors">
    protected THIABBillingRequest(@NonNull Builder<
            IABSKUListKeyType,
            IABSKUType,
            IABSKUListType,
            IABProductDetailType,
            IABPurchaseOrderType,
            IABOrderIdType,
            IABPurchaseType,
            IABExceptionType,
            ?> builder)
    {
        super(builder);
        this.consumePurchase = builder.consumePurchase;
        this.purchaseToConsume = builder.purchaseToConsume;
        this.consumptionFinishedListener = builder.consumptionFinishedListener;
    }
    //</editor-fold>

    public static abstract class Builder<
            IABSKUListKeyType extends IABSKUListKey,
            IABSKUType extends IABSKU,
            IABSKUListType extends BaseIABSKUList<IABSKUType>,
            IABProductDetailType extends IABProductDetail<IABSKUType>,
            IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>
                    & THPurchaseOrder<IABSKUType>,
            IABOrderIdType extends IABOrderId,
            IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>
                    & THProductPurchase<IABSKUType, IABOrderIdType>,
            IABExceptionType extends IABException,
            BuilderType extends Builder<IABSKUListKeyType,
                    IABSKUType,
                    IABSKUListType,
                    IABProductDetailType,
                    IABPurchaseOrderType,
                    IABOrderIdType,
                    IABPurchaseType,
                    IABExceptionType,
                    BuilderType>>
    extends THBillingRequest.Builder<
            IABSKUListKeyType,
            IABSKUType,
            IABSKUListType,
            IABProductDetailType,
            IABPurchaseOrderType,
            IABOrderIdType,
            IABPurchaseType,
            IABExceptionType,
            BuilderType>
    {
        //<editor-fold desc="Constructors">
        protected Builder()
        {
            super();
        }
        //</editor-fold>

        //<editor-fold desc="Whether to Consume Purchase">
        private boolean consumePurchase;
        private IABPurchaseType purchaseToConsume;
        @Nullable private IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType> consumptionFinishedListener;

        public BuilderType consumePurchase(boolean consumePurchase)
        {
            this.consumePurchase = consumePurchase;
            return self();
        }

        public BuilderType purchaseToConsume(IABPurchaseType purchaseToConsume)
        {
            this.purchaseToConsume = purchaseToConsume;
            return self();
        }

        public BuilderType consumptionFinishedListener(
                @Nullable IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType> consumptionFinishedListener)
        {
            this.consumptionFinishedListener = consumptionFinishedListener;
            return self();
        }
        //</editor-fold>

        @Override public THIABBillingRequest<
                IABSKUListKeyType,
                IABSKUType,
                IABSKUListType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType> build()
        {
            return new THIABBillingRequest<>(this);
        }
    }

    @Override public void onDestroy()
    {
        this.consumptionFinishedListener = null;
        super.onDestroy();
    }

    @Override public String toString()
    {
        return "THIABBillingRequest:{" +
                super.toString() +
                ", consumePurchase=" + consumePurchase +
                ", purchaseToConsume=" + purchaseToConsume +
                ", consumptionFinishedListener=" + consumptionFinishedListener +
                '}';
    }
}
