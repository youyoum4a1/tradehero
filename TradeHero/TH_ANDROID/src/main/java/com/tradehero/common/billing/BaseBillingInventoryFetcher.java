package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.util.List;
import java.util.Map;

abstract public class BaseBillingInventoryFetcher<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailsType extends ProductDetail<ProductIdentifierType>,
        BillingExceptionType extends BillingException>
        extends BaseRequestCodeActor
        implements BillingInventoryFetcher<ProductIdentifierType, ProductDetailsType, BillingExceptionType>
{
    private List<ProductIdentifierType> productIdentifiers;

    private OnInventoryFetchedListener<ProductIdentifierType, ProductDetailsType, BillingExceptionType> inventoryListener;

    public BaseBillingInventoryFetcher(int request)
    {
        super(request);
    }

    @Override public List<ProductIdentifierType> getProductIdentifiers()
    {
        return productIdentifiers;
    }

    @Override public void setProductIdentifiers(List<ProductIdentifierType> productIdentifiers)
    {
        this.productIdentifiers = productIdentifiers;
    }

    protected void notifyListenerFetched(Map<ProductIdentifierType, ProductDetailsType> inventory)
    {
        OnInventoryFetchedListener<ProductIdentifierType, ProductDetailsType, BillingExceptionType> listenerCopy = getInventoryFetchedListener();
        if (listenerCopy != null)
        {
            listenerCopy.onInventoryFetchSuccess(getRequestCode(), productIdentifiers, inventory);
        }
    }

    protected void notifyListenerFailed(BillingExceptionType billingException)
    {
        OnInventoryFetchedListener<ProductIdentifierType, ProductDetailsType, BillingExceptionType> listenerCopy = getInventoryFetchedListener();
        if (listenerCopy != null)
        {
            listenerCopy.onInventoryFetchFail(getRequestCode(), productIdentifiers, billingException);
        }
    }

    @Override public OnInventoryFetchedListener<ProductIdentifierType, ProductDetailsType, BillingExceptionType> getInventoryFetchedListener()
    {
        return inventoryListener;
    }

    @Override public void setInventoryFetchedListener(
            OnInventoryFetchedListener<ProductIdentifierType, ProductDetailsType, BillingExceptionType> onInventoryFetchedListener)
    {
        this.inventoryListener = onInventoryFetchedListener;
    }
}
