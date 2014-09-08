package com.tradehero.th.billing.amazon.request;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.amazon.THAmazonOrderId;
import com.tradehero.th.billing.amazon.THAmazonProductDetail;
import com.tradehero.th.billing.amazon.THAmazonPurchase;
import com.tradehero.th.billing.amazon.THAmazonPurchaseOrder;
import org.jetbrains.annotations.NotNull;

public class THAmazonRequestFull
        extends THAmazonRequest<
        AmazonSKUListKey,
        AmazonSKU,
        AmazonSKUList,
        THAmazonProductDetail,
        THAmazonPurchaseOrder,
        THAmazonOrderId,
        THAmazonPurchase,
        AmazonException>
{
    //<editor-fold desc="Constructors">
    protected THAmazonRequestFull(@NotNull Builder<?> builder)
    {
        super(builder);
    }
    //</editor-fold>

    public static abstract class Builder<BuilderType extends Builder<BuilderType>>
            extends THAmazonRequest.Builder<
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
        //<editor-fold desc="Constructors">
        protected Builder()
        {
            super();
        }
        //</editor-fold>

        @Override
        public THAmazonRequestFull build()
        {
            return new THAmazonRequestFull(this);
        }
    }

    private static class Builder2 extends Builder<Builder2>
    {
        //<editor-fold desc="Constructors">
        private Builder2()
        {
            super();
        }
        //</editor-fold>

        @Override protected Builder2 self()
        {
            return this;
        }
    }

    public static Builder<?> builder()
    {
        return new Builder2();
    }
}
