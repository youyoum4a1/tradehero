package com.tradehero.th.billing.request;

import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.BillingRequest;
import com.tradehero.th.billing.PurchaseReporter;

/**
 * Created by xavier on 3/13/14.
 */
public class THBillingRequest<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
        extends BillingRequest<
        ProductIdentifierType,
        ProductDetailType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingExceptionType>
{
    public static final String TAG = THBillingRequest.class.getSimpleName();

    public boolean reportPurchase;
    public ProductPurchaseType purchaseToReport;
    public PurchaseReporter.OnPurchaseReportedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> purchaseReportedListener;

    protected THBillingRequest()
    {
        super();
    }

    @Override public void onDestroy()
    {
        this.purchaseReportedListener = null;
        super.onDestroy();
    }
}
