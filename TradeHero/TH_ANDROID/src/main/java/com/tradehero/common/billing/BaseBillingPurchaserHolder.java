package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BaseBillingPurchaserHolder<
        ProductIdentifierType extends ProductIdentifier,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
    implements BillingPurchaserHolder<
        ProductIdentifierType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingExceptionType>
{
    protected Map<Integer /*requestCode*/, BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType>> purchaseFinishedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType>>> parentPurchaseFinishedListeners;

    public BaseBillingPurchaserHolder()
    {
        super();
        purchaseFinishedListeners = new HashMap<>();
        parentPurchaseFinishedListeners = new HashMap<>();
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return !purchaseFinishedListeners.containsKey(requestCode) &&
                !parentPurchaseFinishedListeners.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        purchaseFinishedListeners.remove(requestCode);
        parentPurchaseFinishedListeners.remove(requestCode);
    }

    /**
     * The listener should be strongly referenced elsewhere.
     * @param purchaseFinishedListener
     * @return
     */
    @Override public void registerPurchaseFinishedListener(int requestCode, BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseFinishedListener)
    {
        parentPurchaseFinishedListeners.put(requestCode, new WeakReference<>(purchaseFinishedListener));
    }

    @Override public BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> getPurchaseFinishedListener(int requestCode)
    {
        WeakReference<BillingPurchaser.OnPurchaseFinishedListener<
                ProductIdentifierType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>> weakHandler = parentPurchaseFinishedListeners.get(requestCode);
        if (weakHandler != null)
        {
            return weakHandler.get();
        }
        return null;
    }

    protected void notifyIABPurchaseFinished(int requestCode, PurchaseOrderType purchaseOrder, ProductPurchaseType purchase)
    {
        Timber.d("notifyIABPurchaseFinished Purchase " + purchase);
        BillingPurchaser.OnPurchaseFinishedListener<
                ProductIdentifierType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> handler = getPurchaseFinishedListener(requestCode);
        if (handler != null)
        {
            Timber.d("notifyIABPurchaseFinished passing on the purchase for requestCode " + requestCode);
            handler.onPurchaseFinished(requestCode, purchaseOrder, purchase);
        }
        else
        {
            Timber.d("notifyIABPurchaseFinished No OnPurchaseFinishedListener for requestCode " + requestCode);
        }
    }

    protected void notifyIABPurchaseFailed(int requestCode, PurchaseOrderType purchaseOrder, BillingExceptionType exception)
    {
        Timber.e("notifyIABPurchaseFailed There was an exception during the purchase", exception);
        BillingPurchaser.OnPurchaseFinishedListener<
                ProductIdentifierType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> handler = getPurchaseFinishedListener(requestCode);
        if (handler != null)
        {
            Timber.d("notifyIABPurchaseFailed passing on the exception for requestCode " + requestCode);
            handler.onPurchaseFailed(requestCode, purchaseOrder, exception);
        }
        else
        {
            Timber.d("onPurchaseFailed No THIABPurchaseHandler for requestCode " + requestCode);
        }
    }

    @Override public void onDestroy()
    {
        purchaseFinishedListeners.clear();
        parentPurchaseFinishedListeners.clear();
    }
}
