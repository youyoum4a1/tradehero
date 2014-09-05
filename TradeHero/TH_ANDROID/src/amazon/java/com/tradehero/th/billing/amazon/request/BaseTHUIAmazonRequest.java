package com.tradehero.th.billing.amazon.request;

import com.tradehero.common.billing.amazon.AmazonPurchaseConsumer;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.amazon.THAmazonOrderId;
import com.tradehero.th.billing.amazon.THAmazonProductDetail;
import com.tradehero.th.billing.amazon.THAmazonPurchase;
import com.tradehero.th.billing.amazon.THAmazonPurchaseOrder;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseTHUIAmazonRequest
        extends BaseTHUIBillingRequest<
                AmazonSKUListKey,
                AmazonSKU,
                AmazonSKUList,
                THAmazonProductDetail,
                THAmazonPurchaseOrder,
                THAmazonOrderId,
                THAmazonPurchase,
                AmazonException>
    implements THUIAmazonRequest
{
    //<editor-fold desc="Purchase Consuming">
    private boolean consumePurchase;

    @Override public boolean getConsumePurchase()
    {
        return consumePurchase;
    }

    @Override public void setConsumePurchase(boolean consumePurchase)
    {
        this.consumePurchase = consumePurchase;
    }

    /**
     * Indicates whether we want the Interactor to pop a dialog when the consume has failed.
     */
    private final boolean popIfConsumeFailed;

    @Override public boolean getPopIfConsumeFailed()
    {
        return popIfConsumeFailed;
    }

    @Nullable private AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
            AmazonSKU,
            THAmazonOrderId,
            THAmazonPurchase,
            AmazonException> consumptionFinishedListener;

    @Nullable @Override public AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
            AmazonSKU,
            THAmazonOrderId,
            THAmazonPurchase,
            AmazonException> getConsumptionFinishedListener()
    {
        return consumptionFinishedListener;
    }

    @Override public void setConsumptionFinishedListener(
            @Nullable AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
                    AmazonSKU,
                    THAmazonOrderId,
                    THAmazonPurchase,
                    AmazonException> consumptionFinishedListener)
    {
        this.consumptionFinishedListener = consumptionFinishedListener;
    }
    //</editor-fold>

    //<editor-fold desc="Constructors">
    protected BaseTHUIAmazonRequest(
            @NotNull Builder<?> builder)
    {
        super(builder);
        this.consumePurchase = builder.consumePurchase;
        this.popIfConsumeFailed = builder.popIfConsumeFailed;
        this.consumptionFinishedListener = builder.consumptionFinishedListener;
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        consumptionFinishedListener = null;
        super.onDestroy();
    }

    @Override public THAmazonRequestFull.Builder<?> createEmptyBillingRequestBuilder()
    {
        return THAmazonRequestFull.builder();

        //if (getDomainToPresent() != null)
        //{
        //    builder.testBillingAvailable(true)
        //            .fetchProductIdentifiers(true)
        //            .fetchInventory(true);
        //}
        //else if (getRestorePurchase())
        //{
        //    builder.testBillingAvailable(true)
        //            .fetchProductIdentifiers(true)
        //            .fetchInventory(true)
        //            .fetchPurchases(true)
        //            .restorePurchase(true);
        //}
        //else if (getFetchInventory())
        //{
        //    builder.testBillingAvailable(true)
        //            .fetchProductIdentifiers(true)
        //            .fetchInventory(true);
        //}
        //else if (getFetchProductIdentifiers())
        //{
        //    builder.testBillingAvailable(true)
        //            .fetchProductIdentifiers(true);
        //}
        //
        //// TODO more?
        //return builder;
    }

    //<editor-fold desc="Builder">
    public static abstract class Builder<
            BuilderType extends Builder<BuilderType>>
            extends BaseTHUIBillingRequest.Builder<
            AmazonSKUListKey,
            AmazonSKU,
            AmazonSKUList,
            THAmazonProductDetail,
            THAmazonPurchaseOrder,
            THAmazonOrderId,
            THAmazonPurchase,
            AmazonException,
            BuilderType>
    {
        //<editor-fold desc="Purchase Consuming">
        private boolean consumePurchase;
        private boolean popIfConsumeFailed;
        @Nullable private AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase,
                AmazonException> consumptionFinishedListener;

        public BuilderType consumePurchase(boolean consumePurchase)
        {
            this.consumePurchase = consumePurchase;
            return self();
        }

        public BuilderType setPopIfConsumeFailed(boolean popIfConsumeFailed)
        {
            this.popIfConsumeFailed = popIfConsumeFailed;
            return self();
        }

        public BuilderType setConsumptionFinishedListener(
                @Nullable AmazonPurchaseConsumer.OnAmazonConsumptionFinishedListener<
                        AmazonSKU,
                        THAmazonOrderId,
                        THAmazonPurchase,
                        AmazonException> consumptionFinishedListener)
        {
            this.consumptionFinishedListener = consumptionFinishedListener;
            return self();
        }
        //</editor-fold>

        @Override public BaseTHUIAmazonRequest build()
        {
            return new BaseTHUIAmazonRequest(this);
        }
    }
    //</editor-fold>

    private static class Builder2 extends Builder<Builder2>
    {
        @Override protected Builder2 self()
        {
            return this;
        }
    }

    public static Builder<?> builder()
    {
        return new Builder2();
    }

    @Override public String toString()
    {
        return "BaseTHUIAmazonRequest:{" +
                super.toString() +
                ", consumePurchase=" + consumePurchase +
                ", popIfConsumeFailed=" + popIfConsumeFailed +
                ", consumptionFinishedListener=" + consumptionFinishedListener +
                '}';
    }
}
