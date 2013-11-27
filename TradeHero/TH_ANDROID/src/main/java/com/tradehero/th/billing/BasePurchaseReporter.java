package com.tradehero.th.billing;

import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
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
                ThrowableType>,
        ThrowableType extends Throwable>
    implements PurchaseReporter<
        ProductIdentifierType,
        OrderIdType,
        ProductPurchaseType,
        OnPurchaseReportedListenerType,
        ThrowableType>
{
    public static final String TAG = BasePurchaseReporter.class.getSimpleName();

    protected int requestCode;
    protected ProductPurchaseType purchase;
    private WeakReference<OnPurchaseReportedListenerType> listener = new WeakReference<>(null);

    public OnPurchaseReportedListenerType getListener()
    {
        return this.listener.get();
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param listener
     */
    public void setListener(final OnPurchaseReportedListenerType listener)
    {
        this.listener = new WeakReference<>(listener);
    }

    protected void notifyListenerSuccess(final UserProfileDTO updatedUserPortfolio)
    {
        OnPurchaseReportedListenerType listener1 = getListener();
        if (listener1 != null)
        {
            listener1.onPurchaseReported(requestCode, this.purchase, updatedUserPortfolio);
        }
    }

    protected void notifyListenerReportFailed(final ThrowableType error)
    {
        OnPurchaseReportedListenerType listener1 = getListener();
        if (listener1 != null)
        {
            listener1.onPurchaseReportFailed(requestCode, this.purchase, error);
        }
    }
}
