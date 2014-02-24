package com.tradehero.common.billing;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABResponse;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABRemoteException;
import com.tradehero.th.base.Application;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import timber.log.Timber;

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

    public List<ProductIdentifierType> getProductIdentifiers()
    {
        return productIdentifiers;
    }

    public void setProductIdentifiers(List<ProductIdentifierType> productIdentifiers)
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
