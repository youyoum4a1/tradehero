package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.request.BillingRequest;

public interface AmazonLogicHolder<
        AmazonSKUListKeyType extends AmazonSKUListKey,
        AmazonSKUType extends AmazonSKU,
        AmazonSKUListType extends BaseAmazonSKUList<AmazonSKUType>,
        AmazonProductDetailType extends AmazonProductDetail<AmazonSKUType>,
        AmazonPurchaseOrderType extends AmazonPurchaseOrder<AmazonSKUType>,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
        BillingRequestType extends BillingRequest<
                AmazonSKUListKeyType,
                AmazonSKUType,
                AmazonSKUListType,
                AmazonProductDetailType,
                AmazonPurchaseOrderType,
                AmazonOrderIdType,
                AmazonPurchaseType,
                AmazonExceptionType>,
        AmazonExceptionType extends AmazonException>
    extends
        BillingLogicHolder<
                AmazonSKUListKeyType,
                AmazonSKUType,
                AmazonSKUListType,
                AmazonProductDetailType,
                AmazonPurchaseOrderType,
                AmazonOrderIdType,
                AmazonPurchaseType,
                BillingRequestType,
                AmazonExceptionType>
{
}
