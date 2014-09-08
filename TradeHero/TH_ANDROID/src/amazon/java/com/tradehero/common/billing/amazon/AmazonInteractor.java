package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.request.UIAmazonRequest;
import com.tradehero.common.billing.request.BillingRequest;
import com.tradehero.common.billing.request.UIBillingRequest;

public interface AmazonInteractor<
        AmazonSKUListKeyType extends AmazonSKUListKey,
        AmazonSKUType extends AmazonSKU,
        AmazonSKUListType extends BaseAmazonSKUList<AmazonSKUType>,
        AmazonProductDetailType extends AmazonProductDetail<AmazonSKUType>,
        AmazonPurchaseOrderType extends AmazonPurchaseOrder<AmazonSKUType>,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<
                AmazonSKUType,
                AmazonOrderIdType>,
        AmazonActorType extends BillingLogicHolder<
                AmazonSKUListKeyType,
                AmazonSKUType,
                AmazonSKUListType,
                AmazonProductDetailType,
                AmazonPurchaseOrderType,
                AmazonOrderIdType,
                AmazonPurchaseType,
                BillingRequestType,
                AmazonExceptionType>,
        BillingRequestType extends BillingRequest<
                AmazonSKUListKeyType,
                AmazonSKUType,
                AmazonSKUListType,
                AmazonProductDetailType,
                AmazonPurchaseOrderType,
                AmazonOrderIdType,
                AmazonPurchaseType,
                AmazonExceptionType>,
        UIBillingRequestType extends UIBillingRequest<
                AmazonSKUListKeyType,
                AmazonSKUType,
                AmazonSKUListType,
                AmazonProductDetailType,
                AmazonPurchaseOrderType,
                AmazonOrderIdType,
                AmazonPurchaseType,
                AmazonExceptionType> & UIAmazonRequest,
        AmazonExceptionType extends AmazonException>
        extends BillingInteractor<
        AmazonSKUListKeyType,
        AmazonSKUType,
        AmazonSKUListType,
        AmazonProductDetailType,
        AmazonPurchaseOrderType,
        AmazonOrderIdType,
        AmazonPurchaseType,
        AmazonActorType,
        BillingRequestType,
        UIBillingRequestType,
        AmazonExceptionType>
{
}
