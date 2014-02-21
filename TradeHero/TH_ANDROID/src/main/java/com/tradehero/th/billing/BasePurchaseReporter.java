package com.tradehero.th.billing;

import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.users.UserProfileDTO;
import java.lang.ref.WeakReference;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:06 PM To change this template use File | Settings | File Templates. */
abstract public class BasePurchaseReporter<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        OnPurchaseReportedListenerType extends PurchaseReporter.OnPurchaseReportedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
    implements PurchaseReporter<
        ProductIdentifierType,
        OrderIdType,
        ProductPurchaseType,
        OnPurchaseReportedListenerType,
        BillingExceptionType>
{
    public static final String TAG = BasePurchaseReporter.class.getSimpleName();

    protected int requestCode;
    protected ProductPurchaseType purchase;
    private OnPurchaseReportedListenerType listener;

    public OnPurchaseReportedListenerType getPurchaseReporterListener()
    {
        return this.listener;
    }

    public void setPurchaseReporterListener(final OnPurchaseReportedListenerType listener)
    {
        this.listener = listener;
    }

    protected void notifyListenerSuccess(final UserProfileDTO updatedUserPortfolio)
    {
        OnPurchaseReportedListenerType listener1 = getPurchaseReporterListener();
        if (listener1 != null)
        {
            listener1.onPurchaseReported(requestCode, this.purchase, updatedUserPortfolio);
        }
    }

    protected void notifyListenerReportFailed(final BillingExceptionType error)
    {
        OnPurchaseReportedListenerType listener1 = getPurchaseReporterListener();
        if (listener1 != null)
        {
            listener1.onPurchaseReportFailed(requestCode, this.purchase, error);
        }
    }
}
