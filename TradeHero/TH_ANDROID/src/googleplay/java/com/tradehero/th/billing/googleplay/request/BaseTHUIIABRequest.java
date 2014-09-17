package com.tradehero.th.billing.googleplay.request;

import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABPurchaseOrder;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseTHUIIABRequest
        extends BaseTHUIBillingRequest<
                        IABSKUListKey,
                        IABSKU,
                        IABSKUList,
                        THIABProductDetail,
                        THIABPurchaseOrder,
                        THIABOrderId,
                        THIABPurchase,
                        IABException>
    implements THUIIABRequest
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

    @Nullable private IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKU,
            THIABOrderId,
            THIABPurchase,
            IABException> consumptionFinishedListener;

    @Nullable @Override public IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKU,
            THIABOrderId,
            THIABPurchase,
            IABException> getConsumptionFinishedListener()
    {
        return consumptionFinishedListener;
    }

    @Override public void setConsumptionFinishedListener(
            @Nullable IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                    IABSKU,
                    THIABOrderId,
                    THIABPurchase,
                    IABException> consumptionFinishedListener)
    {
        this.consumptionFinishedListener = consumptionFinishedListener;
    }
    //</editor-fold>

    //<editor-fold desc="Constructors">
    protected BaseTHUIIABRequest(
            @NotNull BaseTHUIIABRequest.Builder<?> builder)
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

    @Override public THIABBillingRequestFull.Builder<?> createEmptyBillingRequestBuilder()
    {
        return THIABBillingRequestFull.builder();
    }

    //<editor-fold desc="Builder">
    public static abstract class Builder<
            BuilderType extends Builder<BuilderType>>
            extends BaseTHUIBillingRequest.Builder<
            IABSKUListKey,
            IABSKU,
            IABSKUList,
            THIABProductDetail,
            THIABPurchaseOrder,
            THIABOrderId,
            THIABPurchase,
            IABException,
            BuilderType>
    {
        //<editor-fold desc="Purchase Consuming">
        private boolean consumePurchase;
        private boolean popIfConsumeFailed;
        @Nullable private IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKU,
                THIABOrderId,
                THIABPurchase,
                IABException> consumptionFinishedListener;

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
                @Nullable IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                        IABSKU,
                        THIABOrderId,
                        THIABPurchase,
                        IABException> consumptionFinishedListener)
        {
            this.consumptionFinishedListener = consumptionFinishedListener;
            return self();
        }
        //</editor-fold>

        @Override public BaseTHUIIABRequest build()
        {
            return new BaseTHUIIABRequest(this);
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
        return "BaseTHUIIABRequest:{" +
                super.toString() +
                ", consumePurchase=" + consumePurchase +
                ", popIfConsumeFailed=" + popIfConsumeFailed +
                ", consumptionFinishedListener=" + consumptionFinishedListener +
                '}';
    }
}
