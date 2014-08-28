package com.tradehero.th.billing.samsung.request;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.request.THBillingRequest;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungProductDetail;
import com.tradehero.th.billing.samsung.THSamsungPurchase;
import com.tradehero.th.billing.samsung.THSamsungPurchaseOrder;
import org.jetbrains.annotations.NotNull;

public class THSamsungRequestFull
        extends THSamsungRequest<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        THSamsungProductDetail,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase,
        SamsungException>
{
    //<editor-fold desc="Constructors">
    protected THSamsungRequestFull(@NotNull Builder<?> builder)
    {
        super(builder);
    }
    //</editor-fold>

    public static abstract class Builder<BuilderType extends Builder<BuilderType>>
            extends THSamsungRequest.Builder<
            SamsungSKUListKey,
            SamsungSKU,
            SamsungSKUList,
            THSamsungProductDetail,
            THSamsungPurchaseOrder,
            THSamsungOrderId,
            THSamsungPurchase,
            SamsungException,
            BuilderType>
    {
        //<editor-fold desc="Constructors">
        protected Builder()
        {
            super();
        }
        //</editor-fold>

        @Override
        public THSamsungRequestFull build()
        {
            return new THSamsungRequestFull(this);
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
