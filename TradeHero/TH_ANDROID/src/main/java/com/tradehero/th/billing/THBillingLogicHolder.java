package com.tradehero.th.billing;

import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.BillingRequest;

/**
 * Created by xavier on 2/26/14.
 */
public interface THBillingLogicHolder<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>,
        BillingRequestType extends BillingRequest<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                PurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
    extends
        BillingLogicHolder<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                PurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType,
                BillingRequestType,
                BillingExceptionType>,
        THPurchaseReporterHolder<
                        ProductIdentifierType,
                THOrderIdType,
                THProductPurchaseType,
                        BillingExceptionType>,
        ProductDetailDomainInformer<
            ProductIdentifierType,
            ProductDetailType>
{
    void unregisterPurchaseReportedListener(int requestCode);
}
