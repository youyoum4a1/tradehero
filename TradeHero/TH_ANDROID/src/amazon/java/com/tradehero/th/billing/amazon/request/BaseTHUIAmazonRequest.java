package com.tradehero.th.billing.amazon.request;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.amazon.THAmazonOrderId;
import com.tradehero.th.billing.amazon.THAmazonProductDetail;
import com.tradehero.th.billing.amazon.THAmazonPurchase;
import com.tradehero.th.billing.amazon.THAmazonPurchaseOrder;
import com.tradehero.th.billing.amazon.THBaseAmazonPurchase;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import org.jetbrains.annotations.NotNull;

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
    //<editor-fold desc="Constructors">
    protected BaseTHUIAmazonRequest(
            @NotNull Builder<?> builder)
    {
        super(builder);
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
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
}
