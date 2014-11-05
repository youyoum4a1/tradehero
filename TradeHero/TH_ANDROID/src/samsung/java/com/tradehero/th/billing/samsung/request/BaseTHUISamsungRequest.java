package com.tradehero.th.billing.samsung.request;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungProductDetail;
import com.tradehero.th.billing.samsung.THSamsungPurchase;
import com.tradehero.th.billing.samsung.THSamsungPurchaseOrder;
import android.support.annotation.NonNull;

public class BaseTHUISamsungRequest
        extends BaseTHUIBillingRequest<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        THSamsungProductDetail,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase,
        SamsungException>
    implements THUISamsungRequest
{
    //<editor-fold desc="Constructors">
    protected BaseTHUISamsungRequest(
            @NonNull BaseTHUISamsungRequest.Builder<?> builder)
    {
        super(builder);
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public THSamsungRequestFull.Builder<?> createEmptyBillingRequestBuilder()
    {
        return THSamsungRequestFull.builder();

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
        @Override public BaseTHUISamsungRequest build()
        {
            return new BaseTHUISamsungRequest(this);
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
}
