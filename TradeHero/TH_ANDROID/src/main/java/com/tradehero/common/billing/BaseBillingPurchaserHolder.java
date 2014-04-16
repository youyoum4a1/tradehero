package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
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
    protected Map<Integer /*requestCode*/, BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType>> parentPurchaseFinishedListeners;

    public BaseBillingPurchaserHolder()
    {
        super();
        parentPurchaseFinishedListeners = new HashMap<>();
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return !parentPurchaseFinishedListeners.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        parentPurchaseFinishedListeners.remove(requestCode);
    }

    /**
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
        parentPurchaseFinishedListeners.put(requestCode, purchaseFinishedListener);
    }

    @Override public BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> getPurchaseFinishedListener(int requestCode)
    {
        return parentPurchaseFinishedListeners.get(requestCode);
    }

    protected BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType>
    createPurchaseFinishedListener()
    {
        return new BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType>()
        {
            @Override public void onPurchaseFinished(int requestCode, PurchaseOrderType purchaseOrder, ProductPurchaseType purchase)
            {
                notifyPurchaseFinished(requestCode, purchaseOrder, purchase);
            }

            @Override public void onPurchaseFailed(int requestCode, PurchaseOrderType purchaseOrder, BillingExceptionType exception)
            {
                notifyPurchaseFailed(requestCode, purchaseOrder, exception);
            }
        };
    }

    protected void notifyPurchaseFinished(int requestCode, PurchaseOrderType purchaseOrder, ProductPurchaseType purchase)
    {
        Timber.d("notifyPurchaseFinished Purchase " + purchase);
        BillingPurchaser.OnPurchaseFinishedListener<
                ProductIdentifierType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> handler = getPurchaseFinishedListener(requestCode);
        if (handler != null)
        {
            Timber.d("notifyPurchaseFinished passing on the purchase for requestCode " + requestCode);
            handler.onPurchaseFinished(requestCode, purchaseOrder, purchase);
        }
        else
        {
            Timber.d("notifyPurchaseFinished No OnPurchaseFinishedListener for requestCode " + requestCode);
        }
    }

    protected void notifyPurchaseFailed(int requestCode, PurchaseOrderType purchaseOrder, BillingExceptionType exception)
    {
        Timber.e("notifyPurchaseFailed There was an exception during the purchase", exception);
        BillingPurchaser.OnPurchaseFinishedListener<
                ProductIdentifierType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> handler = getPurchaseFinishedListener(requestCode);
        if (handler != null)
        {
            Timber.d("notifyPurchaseFailed passing on the exception for requestCode " + requestCode);
            handler.onPurchaseFailed(requestCode, purchaseOrder, exception);
        }
        else
        {
            Timber.d("onPurchaseFailed No THIABPurchaseHandler for requestCode " + requestCode);
        }
    }

    @Override public void onDestroy()
    {
        parentPurchaseFinishedListeners.clear();
    }
}
