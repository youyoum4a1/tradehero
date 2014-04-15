package com.tradehero.th.billing;

import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.users.UserProfileDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:06 PM To change this template use File | Settings | File Templates. */
abstract public class BasePurchaseReporter<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
    implements PurchaseReporter<
        ProductIdentifierType,
        OrderIdType,
        THProductPurchaseType,
        BillingExceptionType>
{
    public static final String TAG = BasePurchaseReporter.class.getSimpleName();

    protected int requestCode;
    protected THProductPurchaseType purchase;
    private PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, THProductPurchaseType, BillingExceptionType> listener;

    @Override public PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, THProductPurchaseType, BillingExceptionType> getPurchaseReporterListener()
    {
        return this.listener;
    }

    @Override public void setPurchaseReporterListener(final PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, THProductPurchaseType, BillingExceptionType> listener)
    {
        this.listener = listener;
    }

    protected void notifyListenerSuccess(final UserProfileDTO updatedUserPortfolio)
    {
        PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, THProductPurchaseType, BillingExceptionType> listener1 = getPurchaseReporterListener();
        if (listener1 != null)
        {
            listener1.onPurchaseReported(requestCode, this.purchase, updatedUserPortfolio);
        }
    }

    protected void notifyListenerReportFailed(final BillingExceptionType error)
    {
        PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, THProductPurchaseType, BillingExceptionType> listener1 = getPurchaseReporterListener();
        if (listener1 != null)
        {
            listener1.onPurchaseReportFailed(requestCode, this.purchase, error);
        }
    }
}
