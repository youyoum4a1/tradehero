package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.users.UserProfileDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:06 PM To change this template use File | Settings | File Templates. */
abstract public class THBasePurchaseReporter<
        ProductIdentifierType extends ProductIdentifier,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>,
        BillingExceptionType extends BillingException>
        implements THPurchaseReporter<
        ProductIdentifierType,
        THOrderIdType,
        THProductPurchaseType,
        BillingExceptionType>
{
    public static final String TAG = THBasePurchaseReporter.class.getSimpleName();

    protected int requestCode;
    protected THProductPurchaseType purchase;
    private THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> listener;

    @Override public int getRequestCode()
    {
        return requestCode;
    }

    @Override public THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> getPurchaseReporterListener()
    {
        return this.listener;
    }

    @Override public void setPurchaseReporterListener(final THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> listener)
    {
        this.listener = listener;
    }

    protected void notifyListenerSuccess(final UserProfileDTO updatedUserPortfolio)
    {
        THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> listener1 = getPurchaseReporterListener();
        if (listener1 != null)
        {
            listener1.onPurchaseReported(requestCode, this.purchase, updatedUserPortfolio);
        }
    }

    protected void notifyListenerReportFailed(final BillingExceptionType error)
    {
        THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> listener1 = getPurchaseReporterListener();
        if (listener1 != null)
        {
            listener1.onPurchaseReportFailed(requestCode, this.purchase, error);
        }
    }
}
