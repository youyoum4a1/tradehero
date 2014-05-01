package com.tradehero.th.billing.request;

import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.OnFollowResultListener;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.PurchaseReporter;

public class THUIBillingRequest<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
        extends UIBillingRequest<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        ProductDetailType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingExceptionType>
{
    /**
     * The domain of product identifiers to present to the user.
     */
    public ProductIdentifierDomain domainToPresent;

    /**
     * Indicates whether we want the Interactor to pop a dialog when reporting fails
     */
    public boolean popIfReportFailed;
    public PurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseReportedListener;

    public OnFollowResultListener followResultListener;
    public UserBaseKey userToFollow;

    public THUIBillingRequest()
    {
        super();
    }

    public void onDestroy()
    {
        this.followResultListener = null;
    }
}
