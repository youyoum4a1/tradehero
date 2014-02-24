package com.tradehero.common.billing.googleplay;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.exception.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABRemoteException;
import com.tradehero.th.base.Application;
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
abstract public class IABBillingInventoryFetcher<
            IABSKUType extends IABSKU,
            IABProductDetailsType extends IABProductDetail<IABSKUType>>
        extends IABServiceConnector
        implements BillingInventoryFetcher<
            IABSKUType,
            IABProductDetailsType,
            IABException>
{
    protected HashMap<IABSKUType, IABProductDetailsType> inventory;
    private List<IABSKUType> iabSKUs;
    private int requestCode;

    private OnInventoryFetchedListener<IABSKUType, IABProductDetailsType, IABException> inventoryListener;

    public IABBillingInventoryFetcher()
    {
        super();
        this.inventory = new HashMap<>();
    }

    @Override public List<IABSKUType> getProductIdentifiers()
    {
        return iabSKUs;
    }

    @Override public void setProductIdentifiers(List<IABSKUType> productIdentifiers)
    {
        this.iabSKUs = productIdentifiers;
    }

    @Override public int getRequestCode()
    {
        return requestCode;
    }

    abstract protected IABProductDetailsType createSKUDetails(String itemType, String json) throws JSONException;

    @Override public void fetchInventory(int requestCode)
    {
        this.requestCode = requestCode;
        this.startConnectionSetup();
    }

    @Override protected void handleSetupFailed(IABException exception)
    {
        super.handleSetupFailed(exception);
        handleInventoryFetchFailure(exception);
    }

    @Override protected void handleSetupFinished(IABResponse response)
    {
        fetchInventoryAsync();
    }

    private void fetchInventoryAsync()
    {
        AsyncTask<Void, Void, HashMap<IABSKUType, IABProductDetailsType>> backgroundTask =  new AsyncTask<Void, Void, HashMap<IABSKUType, IABProductDetailsType>>()
        {
            private IABException exception;

            @Override protected HashMap<IABSKUType, IABProductDetailsType> doInBackground(Void... params)
            {
                if (!disposed)
                {
                    try
                    {
                        return internalFetchCompleteInventory();
                    }
                    catch (RemoteException e)
                    {
                        Timber.e("Remote Exception while fetching inventory.", e);
                        exception = new IABRemoteException("RemoteException while fetching IAB", e);
                    }
                    catch (JSONException e)
                    {
                        Timber.e("Error parsing json.", e);
                        exception = new IABBadResponseException("Unable to parse JSON", e);
                    }
                    catch (IABException e)
                    {
                        Timber.e("IAB error.", e);
                        exception = e;
                    }
                }
                return null;
            }

            @Override protected void onPostExecute(HashMap<IABSKUType, IABProductDetailsType> skuskuDetailsMap)
            {
                if (disposed)
                {
                    // Nothing to do
                }
                else if (exception != null)
                {
                    handleInventoryFetchFailure(exception);
                }
                else
                {
                    inventory = skuskuDetailsMap;
                    notifyListenerFetched();
                }
            }
        };
        backgroundTask.execute();
    }

    private void handleInventoryFetchFailure(IABException e)
    {
        OnInventoryFetchedListener<IABSKUType, IABProductDetailsType, IABException> listenerCopy = getInventoryFetchedListener();
        if (listenerCopy != null)
        {
            listenerCopy.onInventoryFetchFail(requestCode, iabSKUs, e);
        }
    }

    protected void notifyListenerFetched()
    {
        OnInventoryFetchedListener<IABSKUType, IABProductDetailsType, IABException> listenerCopy = getInventoryFetchedListener();
        if (listenerCopy != null)
        {
            listenerCopy.onInventoryFetchSuccess(requestCode, iabSKUs, this.getInventory());
        }
    }

    protected HashMap<IABSKUType, IABProductDetailsType> internalFetchCompleteInventory() throws IABException, RemoteException, JSONException
    {
        if (iabSKUs == null || iabSKUs.isEmpty())
        {
            return new HashMap<>();
        }

        HashMap<IABSKUType, IABProductDetailsType> map = internalFetchSKUType(IABConstants.ITEM_TYPE_INAPP);

        if (areSubscriptionsSupported())
        {
            HashMap<IABSKUType, IABProductDetailsType> subscriptionsMap = internalFetchSKUType(
                    IABConstants.ITEM_TYPE_SUBS);
            map.putAll(subscriptionsMap);
        }

        return map;
    }

    private Bundle getQuerySKUBundle()
    {
        ArrayList<String> identifiers = new ArrayList<>(this.iabSKUs.size());
        for (IABSKU iabSKU : this.iabSKUs)
        {
            identifiers.add(iabSKU.identifier);
        }
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList(IABConstants.GET_SKU_DETAILS_ITEM_LIST, identifiers);
        return querySkus;
    }

    private HashMap<IABSKUType, IABProductDetailsType> internalFetchSKUType(String itemType) throws IABException, RemoteException, JSONException
    {
        Bundle querySkus = getQuerySKUBundle();
        // throws NullPointerException still makes app crash, use global application context to get package name instead

        //if (context == null)
        //{
        //    throw new NullPointerException("Context cannot be null");
        //}

        // TODO still crashing with NullPointerException
        if (this.billingService == null)
        {
            throw new NullPointerException("billingService cannot be null");
        }
        Bundle skuDetails = this.billingService.getSkuDetails(TARGET_BILLING_API_VERSION3, Application.context().getPackageName(), itemType, querySkus);

        if (!skuDetails.containsKey(IABConstants.RESPONSE_GET_SKU_DETAILS_LIST))
        {
            int statusCode = IABConstants.getResponseCodeFromBundle(skuDetails);
            if (statusCode != IABConstants.BILLING_RESPONSE_RESULT_OK)
            {
                Timber.d("getSkuDetails() failed: %s", IABConstants.getStatusCodeDescription(
                        statusCode));
                throw iabExceptionFactory.get().create(statusCode);
            }
            else
            {
                Timber.d("getSkuDetails() returned a bundle with neither an error nor a detail list.");
                throw new IABBadResponseException(IABConstants.getStatusCodeDescription(statusCode));
            }
        }

        ArrayList<String> responseList = skuDetails.getStringArrayList(IABConstants.RESPONSE_GET_SKU_DETAILS_LIST);

        HashMap<IABSKUType, IABProductDetailsType> map = new HashMap<>();
        for (String json : responseList)
        {
            IABProductDetailsType details = createSKUDetails(itemType, json);
            Timber.d("Got iabSKU details: %s", details);
            map.put(details.getProductIdentifier(), details);
        }
        return map;
    }

    public Map<IABSKUType, IABProductDetailsType> getInventory()
    {
        return Collections.unmodifiableMap(inventory);
    }

    @Override public OnInventoryFetchedListener<IABSKUType, IABProductDetailsType, IABException> getInventoryFetchedListener()
    {
        return inventoryListener;
    }

    @Override public void setInventoryFetchedListener(OnInventoryFetchedListener<IABSKUType, IABProductDetailsType, IABException> onInventoryFetchedListener)
    {
        this.inventoryListener = onInventoryFetchedListener;
    }
}
