package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BaseBillingLogicHolder<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingRequestType extends BillingRequest<
                ProductIdentifierType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
    implements BillingLogicHolder<
        ProductIdentifierType,
        ProductDetailType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingRequestType,
        BillingExceptionType>
{
    public static final int MAX_RANDOM_RETRIES = 50;

    protected Boolean billingAvailable = null;
    protected Map<Integer, OnBillingAvailableListener<BillingExceptionType>> billingAvailableListeners;

    public BaseBillingLogicHolder()
    {
        super();
        billingAvailableListeners = new HashMap<>();
        testBillingAvailable();
    }

    @Override public void onDestroy()
    {
        if (billingAvailableListeners != null)
        {
            billingAvailableListeners.clear();
        }
    }

    //<editor-fold desc="Request Code Management">
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

    @Override public boolean isUnusedRequestCode(int randomNumber)
    {
        return !billingAvailableListeners.containsKey(randomNumber);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        billingAvailableListeners.remove(requestCode);
    }
    //</editor-fold>

    @Override public void registerListeners(int requestCode, BillingRequestType billingRequest)
    {
        registerBillingAvailableListener(requestCode, billingRequest.getBillingAvailableListener());
    }

    //<editor-fold desc="Billing Available">
    @Override public Boolean isBillingAvailable()
    {
        return billingAvailable;
    }

    abstract protected void testBillingAvailable();

    @Override public void registerBillingAvailableListener(int requestCode,
            OnBillingAvailableListener<BillingExceptionType> billingAvailableListener)
    {
        billingAvailableListeners.put(requestCode, billingAvailableListener);
    }

    protected void notifyBillingAvailable()
    {
        billingAvailable = true;
        OnBillingAvailableListener<BillingExceptionType> availableListener;
        // Protect from unsync when unregistering the listeners
        for (Integer requestCode : new ArrayList<>(billingAvailableListeners.keySet()))
        {
            availableListener = billingAvailableListeners.get(requestCode);
            if (availableListener != null)
            {
                availableListener.onBillingAvailable();
            }
            billingAvailableListeners.remove(requestCode);
        }
    }

    protected void notifyBillingNotAvailable(BillingExceptionType exception)
    {
        billingAvailable = false;
        OnBillingAvailableListener<BillingExceptionType> availableListener;
        // Protect from unsync when unregistering the listeners
        for (Integer requestCode : new ArrayList<>(billingAvailableListeners.keySet()))
        {
            availableListener = billingAvailableListeners.get(requestCode);
            if (availableListener != null)
            {
                availableListener.onBillingNotAvailable(exception);
            }
            billingAvailableListeners.remove(requestCode);
        }
    }
    //</editor-fold>
}
