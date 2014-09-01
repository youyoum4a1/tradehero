package com.tradehero.th.billing.amazon.request;

import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.tradehero.common.billing.amazon.AmazonProductDetail;
import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonPurchaseConsumer;
import com.tradehero.common.billing.amazon.AmazonPurchaseOrder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.BaseAmazonSKUList;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.THProductPurchase;
import com.tradehero.th.billing.THPurchaseOrder;
import com.tradehero.th.billing.request.THBillingRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class THAmazonRequest<
        AmazonSKUListKeyType extends AmazonSKUListKey,
        AmazonSKUType extends AmazonSKU,
        AmazonSKUListType extends BaseAmazonSKUList<AmazonSKUType>,
        AmazonProductDetailType extends AmazonProductDetail<AmazonSKUType>,
        THAmazonPurchaseOrderType extends AmazonPurchaseOrder<AmazonSKUType>
                & THPurchaseOrder<AmazonSKUType>,
        AmazonOrderIdType extends AmazonOrderId,
        THAmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>
                & THProductPurchase<AmazonSKUType, AmazonOrderIdType>,
        AmazonExceptionType extends AmazonException>
        extends THBillingRequest<
        AmazonSKUListKeyType,
        AmazonSKUType,
        AmazonSKUListType,
        AmazonProductDetailType,
        THAmazonPurchaseOrderType,
        AmazonOrderIdType,
        THAmazonPurchaseType,
        AmazonExceptionType>
{
    public boolean consumePurchase;
    public THAmazonPurchaseType purchaseToConsume;
    @Nullable public AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
            AmazonSKUType,
            AmazonOrderIdType,
            THAmazonPurchaseType,
            AmazonExceptionType> consumptionFinishedListener;

    //<editor-fold desc="Constructors">
    protected THAmazonRequest(@NotNull Builder<
            AmazonSKUListKeyType,
            AmazonSKUType,
            AmazonSKUListType,
            AmazonProductDetailType,
            THAmazonPurchaseOrderType,
            AmazonOrderIdType,
            THAmazonPurchaseType,
            AmazonExceptionType,
            ?> builder)
    {
        super(builder);
        this.consumePurchase = builder.consumePurchase;
        this.purchaseToConsume = builder.purchaseToConsume;
        this.consumptionFinishedListener = builder.consumptionFinishedListener;
    }
    //</editor-fold>

    public static abstract class Builder<
            AmazonSKUListKeyType extends AmazonSKUListKey,
            AmazonSKUType extends AmazonSKU,
            AmazonSKUListType extends BaseAmazonSKUList<AmazonSKUType>,
            AmazonProductDetailType extends AmazonProductDetail<AmazonSKUType>,
            AmazonPurchaseOrderType extends AmazonPurchaseOrder<AmazonSKUType>
                    & THPurchaseOrder<AmazonSKUType>,
            AmazonOrderIdType extends AmazonOrderId,
            AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>
                    & THProductPurchase<AmazonSKUType, AmazonOrderIdType>,
            AmazonExceptionType extends AmazonException,
            BuilderType extends Builder<AmazonSKUListKeyType,
                    AmazonSKUType,
                    AmazonSKUListType,
                    AmazonProductDetailType,
                    AmazonPurchaseOrderType,
                    AmazonOrderIdType,
                    AmazonPurchaseType,
                    AmazonExceptionType,
                    BuilderType>>
            extends THBillingRequest.Builder<
            AmazonSKUListKeyType,
            AmazonSKUType,
            AmazonSKUListType,
            AmazonProductDetailType,
            AmazonPurchaseOrderType,
            AmazonOrderIdType,
            AmazonPurchaseType,
            AmazonExceptionType,
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
        private AmazonPurchaseType purchaseToConsume;
        @Nullable private AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
                AmazonSKUType,
                AmazonOrderIdType,
                AmazonPurchaseType,
                AmazonExceptionType> consumptionFinishedListener;

        public BuilderType consumePurchase(boolean consumePurchase)
        {
            this.consumePurchase = consumePurchase;
            return self();
        }

        public BuilderType purchaseToConsume(AmazonPurchaseType purchaseToConsume)
        {
            this.purchaseToConsume = purchaseToConsume;
            return self();
        }

        public BuilderType consumptionFinishedListener(
                @Nullable AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
                        AmazonSKUType,
                        AmazonOrderIdType,
                        AmazonPurchaseType,
                        AmazonExceptionType> consumptionFinishedListener)
        {
            this.consumptionFinishedListener = consumptionFinishedListener;
            return self();
        }
        //</editor-fold>

        @Override public THAmazonRequest<
                AmazonSKUListKeyType,
                AmazonSKUType,
                AmazonSKUListType,
                AmazonProductDetailType,
                AmazonPurchaseOrderType,
                AmazonOrderIdType,
                AmazonPurchaseType,
                AmazonExceptionType> build()
        {
            return new THAmazonRequest<>(this);
        }
    }

    @Override public void onDestroy()
    {
        this.consumptionFinishedListener = null;
        super.onDestroy();
    }

    @Override public String toString()
    {
        return "THAmazonBillingRequest:{" +
                super.toString() +
                ", consumePurchase=" + consumePurchase +
                ", purchaseToConsume=" + purchaseToConsume +
                ", consumptionFinishedListener=" + consumptionFinishedListener +
                '}';
    }
}
