package com.tradehero.th.billing;

import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.billing.request.THBillingRequest;
import com.tradehero.th.billing.request.THUIBillingRequest;

/**
 * Created by xavier on 2/24/14.
 */
public interface THBillingInteractor<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<
                ProductIdentifierType,
                THOrderIdType>,
        THBillingLogicHolderType extends THBillingLogicHolder<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType,
                THBillingRequestType,
                BillingExceptionType>,
        THBillingRequestType extends THBillingRequest<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType,
                BillingExceptionType>,
        THUIBillingRequestType extends THUIBillingRequest<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
        extends BillingInteractor<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        ProductDetailType,
        THPurchaseOrderType,
        THOrderIdType,
        THProductPurchaseType,
        THBillingLogicHolderType,
        THBillingRequestType,
        THUIBillingRequestType,
        BillingExceptionType>,
        BillingAlertDialogUtil.OnDialogProductDetailClickListener<ProductDetailType>
{
}
