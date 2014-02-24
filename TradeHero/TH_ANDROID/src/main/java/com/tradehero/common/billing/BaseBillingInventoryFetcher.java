package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.util.List;
import java.util.Map;

/**
 * Created by julien on 4/11/13
 */
abstract public class BaseBillingInventoryFetcher<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailsType extends ProductDetail<ProductIdentifierType>,
        BillingExceptionType extends BillingException>
        implements BillingInventoryFetcher<ProductIdentifierType, ProductDetailsType, BillingExceptionType>
{
    private List<ProductIdentifierType> productIdentifiers;
    private int requestCode;

    private OnInventoryFetchedListener<ProductIdentifierType, ProductDetailsType, BillingExceptionType> inventoryListener;

    public BaseBillingInventoryFetcher()
    {
        super();
    }

    @Override public List<ProductIdentifierType> getProductIdentifiers()
    {
        return productIdentifiers;
    }

    @Override public void setProductIdentifiers(List<ProductIdentifierType> productIdentifiers)
    {
        this.productIdentifiers = productIdentifiers;
    }

    @Override public int getRequestCode()
    {
        return requestCode;
    }

    @Override public void fetchInventory(int requestCode)
    {
        this.requestCode = requestCode;
    }

    protected void notifyListenerFetched(Map<ProductIdentifierType, ProductDetailsType> inventory)
    {
        OnInventoryFetchedListener<ProductIdentifierType, ProductDetailsType, BillingExceptionType> listenerCopy = getInventoryFetchedListener();
        if (listenerCopy != null)
        {
            listenerCopy.onInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
        }
    }

    protected void notifyListenerFailed(BillingExceptionType billingException)
    {
        OnInventoryFetchedListener<ProductIdentifierType, ProductDetailsType, BillingExceptionType> listenerCopy = getInventoryFetchedListener();
        if (listenerCopy != null)
        {
            listenerCopy.onInventoryFetchFail(requestCode, productIdentifiers, billingException);
        }
    }

    @Override public OnInventoryFetchedListener<ProductIdentifierType, ProductDetailsType, BillingExceptionType> getInventoryFetchedListener()
    {
        return inventoryListener;
    }

    @Override public void setInventoryFetchedListener(OnInventoryFetchedListener<ProductIdentifierType, ProductDetailsType, BillingExceptionType> onInventoryFetchedListener)
    {
        this.inventoryListener = onInventoryFetchedListener;
    }
}
