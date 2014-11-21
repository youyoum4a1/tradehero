package com.tradehero.th.billing.googleplay.request;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABPurchaseOrder;

public class THIABBillingRequestFull extends THIABBillingRequest<
        IABSKUListKey,
        IABSKU,
        IABSKUList,
        THIABProductDetail,
        THIABPurchaseOrder,
        THIABOrderId,
        THIABPurchase,
        IABException>
{
    //<editor-fold desc="Constructors">
    protected THIABBillingRequestFull(@NonNull Builder<?> builder)
    {
        super(builder);
    }
    //</editor-fold>

    public static abstract class Builder<BuilderType extends Builder<BuilderType>>
        extends THIABBillingRequest.Builder<
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
        //<editor-fold desc="Constructors">
        protected Builder()
        {
            super();
        }
        //</editor-fold>

        @Override
        public THIABBillingRequestFull build()
        {
            return new THIABBillingRequestFull(this);
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
