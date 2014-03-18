package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.billing.request.THBillingRequest;
import com.tradehero.th.billing.request.THUIBillingRequest;

/**
 * Created by xavier on 2/24/14.
 */
public interface THBillingInteractor<
            ProductIdentifierType extends ProductIdentifier,
            ProductDetailType extends ProductDetail<ProductIdentifierType>,
            PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
            OrderIdType extends OrderId,
            ProductPurchaseType extends ProductPurchase<
                    ProductIdentifierType,
                    OrderIdType>,
            THBillingLogicHolderType extends THBillingLogicHolder<
                    ProductIdentifierType,
                    ProductDetailType,
                    PurchaseOrderType,
                    OrderIdType,
                    ProductPurchaseType,
                    THBillingRequestType,
                    BillingExceptionType>,
            THBillingRequestType extends THBillingRequest<
                            ProductIdentifierType,
                            ProductDetailType,
                            PurchaseOrderType,
                            OrderIdType,
                            ProductPurchaseType,
                            BillingExceptionType>,
            THUIBillingRequestType extends THUIBillingRequest<
                    ProductIdentifierType,
                    ProductDetailType,
                    PurchaseOrderType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType>,
            BillingExceptionType extends BillingException>
        extends BillingInteractor<
            ProductIdentifierType,
            ProductDetailType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            THBillingLogicHolderType,
            THBillingRequestType,
            THUIBillingRequestType,
            BillingExceptionType>,
        BillingAlertDialogUtil.OnDialogProductDetailClickListener<ProductDetailType>
{
}
