package com.tradehero.th.billing;

import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.api.users.UserProfileDTO;
import java.lang.ref.WeakReference;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:06 PM To change this template use File | Settings | File Templates. */
abstract public class BasePurchaseReporter<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
{
    public static final String TAG = BasePurchaseReporter.class.getSimpleName();

    protected int requestCode;
    protected IABPurchaseType purchase;
    private WeakReference<OnPurchaseReportedListener<IABSKUType, IABOrderIdType, IABPurchaseType>> listener = new WeakReference<>(null);

    abstract public void reportPurchase(int requestCode, final IABPurchaseType purchase);
    abstract public UserProfileDTO reportPurchaseSync(final IABPurchaseType purchase);

    public OnPurchaseReportedListener<IABSKUType, IABOrderIdType, IABPurchaseType> getListener()
    {
        return this.listener.get();
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param listener
     */
    public void setListener(final OnPurchaseReportedListener<IABSKUType, IABOrderIdType, IABPurchaseType> listener)
    {
        this.listener = new WeakReference<>(listener);
    }

    protected void notifyListenerSuccess(final UserProfileDTO updatedUserPortfolio)
    {
        OnPurchaseReportedListener<IABSKUType, IABOrderIdType, IABPurchaseType> listener1 = getListener();
        if (listener1 != null)
        {
            listener1.onPurchaseReported(requestCode, this.purchase, updatedUserPortfolio);
        }
    }

    protected void notifyListenerReportFailed(final Throwable error)
    {
        OnPurchaseReportedListener<IABSKUType, IABOrderIdType, IABPurchaseType> listener1 = getListener();
        if (listener1 != null)
        {
            listener1.onPurchaseReportFailed(requestCode, this.purchase, error);
        }
    }

    public static interface OnPurchaseReportedListener<
            IABSKUType extends IABSKU,
            IABOrderIdType extends IABOrderId,
            IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
    {
        void onPurchaseReported(int requestCode, IABPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio);
        void onPurchaseReportFailed(int requestCode, IABPurchaseType reportedPurchase, Throwable error);
    }
}
