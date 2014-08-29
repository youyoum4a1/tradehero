package com.tradehero.th.billing.amazon.request;

import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.tradehero.common.billing.amazon.AmazonProductDetail;
import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonPurchaseOrder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.BaseAmazonSKUList;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.THProductPurchase;
import com.tradehero.th.billing.THPurchaseOrder;
import com.tradehero.th.billing.request.THBillingRequest;
import org.jetbrains.annotations.NotNull;

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
    }
    //</editor-fold>
}
