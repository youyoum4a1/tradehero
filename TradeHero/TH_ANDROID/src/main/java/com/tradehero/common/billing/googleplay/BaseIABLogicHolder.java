package com.tradehero.common.billing.googleplay;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.ProductIdentifierFetcherHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.lang.ref.WeakReference;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
abstract public class BaseIABLogicHolder<
        IABSKUType extends IABSKU,
        IABProductIdentifierFetcherHolderType extends ProductIdentifierFetcherHolder<
                IABSKUType,
                IABProductIdentifierFetchedListenerType,
                IABException>,
        IABProductIdentifierFetchedListenerType extends ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                IABSKUType,
                IABException>,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABInventoryFetcherHolderType extends IABInventoryFetcherHolder<
                IABSKUType,
                IABProductDetailType,
                IABInventoryFetchedListenerType,
                IABException>,
        IABInventoryFetchedListenerType extends BillingInventoryFetcher.OnInventoryFetchedListener<
                IABSKUType,
                IABProductDetailType,
                IABException>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<
                IABSKUType,
                IABOrderIdType>,
        IABPurchaseFetcherHolderType extends IABPurchaseFetcherHolder<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABPurchaseFetchedListenerType,
                IABException>,
        IABPurchaseFetchedListenerType extends BillingPurchaseFetcher.OnPurchaseFetchedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>,
        IABPurchaserHolderType extends IABPurchaserHolder<
                IABSKUType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABPurchaseFinishedListenerType,
                IABException>,
        IABPurchaseFinishedListenerType extends BillingPurchaser.OnPurchaseFinishedListener<
                IABSKUType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>,
        IABPurchaseConsumerHolderType extends IABPurchaseConsumerHolder<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABConsumeFinishedListenerType,
                IABException>,
        IABConsumeFinishedListenerType extends IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>>
    implements IABLogicHolder<
            IABSKUType,
            IABProductDetailType,
            IABPurchaseOrderType,
            IABOrderIdType,
            IABPurchaseType,
            IABException>
{
    public static final int MAX_RANDOM_RETRIES = 50;

    protected WeakReference<Activity> weakActivity = new WeakReference<>(null);

    protected IABProductIdentifierFetcherHolderType productIdentifierFetcherHolder;
    protected IABInventoryFetcherHolderType inventoryFetcherHolder;
    protected IABPurchaseFetcherHolderType purchaseFetcherHolder;
    protected IABPurchaserHolderType purchaserHolder;
    protected IABPurchaseConsumerHolderType purchaseConsumerHolder;

    public BaseIABLogicHolder(Activity activity)
    {
        super();
        setActivity(activity);

        productIdentifierFetcherHolder = createProductIdentifierFetcherHolder();
        inventoryFetcherHolder = createInventoryFetcherHolder();
        purchaseFetcherHolder = createPurchaseFetcherHolder();
        purchaserHolder = createPurchaserHolder();
        purchaseConsumerHolder = createPurchaseConsumeHolder();
    }

    public void onDestroy()
    {
        if (productIdentifierFetcherHolder != null)
        {
            productIdentifierFetcherHolder.onDestroy();
        }

        if (inventoryFetcherHolder != null)
        {
            inventoryFetcherHolder.onDestroy();
        }

        if (purchaserHolder != null)
        {
            purchaserHolder.onDestroy();
        }

        if (purchaseConsumerHolder != null)
        {
            purchaseConsumerHolder.onDestroy();
        }
    }

    public Activity getActivity()
    {
        return weakActivity.get();
    }

    /**
     * The activity should be strongly referenced elsewhere
     * @param activity
     */
    public void setActivity(Activity activity)
    {
        this.weakActivity = new WeakReference<>(activity);
    }

    @Override public boolean isBillingAvailable() // TODO review to make less HACKy
    {
        return true;
    }

    @Override public int getUnusedRequestCode()
    {
        int retries = MAX_RANDOM_RETRIES;
        int randomNumber;
        while (retries-- > 0)
        {
            randomNumber = (int) (Math.random() * Integer.MAX_VALUE);
            if (isUnusedRequestCode(randomNumber))
            {
                return randomNumber;
            }
        }
        throw new IllegalStateException("Could not find an unused requestCode after " + MAX_RANDOM_RETRIES + " trials");
    }

    public boolean isUnusedRequestCode(int randomNumber)
    {
        return
                productIdentifierFetcherHolder.isUnusedRequestCode(randomNumber) &&
                inventoryFetcherHolder.isUnusedRequestCode(randomNumber) &&
                purchaseFetcherHolder.isUnusedRequestCode(randomNumber) &&
                purchaserHolder.isUnusedRequestCode(randomNumber) &&
                purchaseConsumerHolder.isUnusedRequestCode(randomNumber);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        productIdentifierFetcherHolder.unregisterProductIdentifierFetchedListener(requestCode);
        inventoryFetcherHolder.unRegisterInventoryFetchedListener(requestCode);
        purchaseFetcherHolder.unregisterPurchaseFetchedListener(requestCode);
        purchaserHolder.unregisterPurchaseFinishedListener(requestCode);
        purchaseConsumerHolder.unregisterConsumeFinishedListener(requestCode);
    }

    abstract protected BaseIABSKUList<IABSKUType> getAllSkus();
    abstract protected IABProductIdentifierFetcherHolderType createProductIdentifierFetcherHolder();
    abstract protected IABInventoryFetcherHolderType createInventoryFetcherHolder();
    abstract protected IABPurchaseFetcherHolderType createPurchaseFetcherHolder();
    abstract protected IABPurchaserHolderType createPurchaserHolder();
    abstract protected IABPurchaseConsumerHolderType createPurchaseConsumeHolder();

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        purchaserHolder.onActivityResult(requestCode, resultCode, data);
    }
}
