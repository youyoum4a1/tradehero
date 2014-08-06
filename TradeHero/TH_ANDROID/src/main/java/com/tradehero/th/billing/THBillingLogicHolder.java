package com.tradehero.th.billing;

import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.BillingRequest;

public interface THBillingLogicHolder<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>,
        BillingRequestType extends BillingRequest<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                THProductDetailType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
        extends
        BillingLogicHolder<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                THProductDetailType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType,
                BillingRequestType,
                BillingExceptionType>,
        THPurchaseReporterHolder<
                ProductIdentifierType,
                THOrderIdType,
                THProductPurchaseType,
                BillingExceptionType>,
        THProductDetailDomainInformer<
                ProductIdentifierType,
                THProductDetailType>
{
    void unregisterPurchaseReportedListener(int requestCode);
}
