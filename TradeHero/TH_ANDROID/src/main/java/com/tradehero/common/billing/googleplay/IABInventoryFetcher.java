package com.tradehero.common.billing.googleplay;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import com.tradehero.common.billing.InventoryFetcher;
import com.tradehero.common.billing.googleplay.exceptions.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.billing.googleplay.exceptions.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.exceptions.IABRemoteException;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import javax.inject.Inject;
import org.json.JSONException;

import java.util.*;

/**
 * Created by julien on 4/11/13
 */
abstract public class IABInventoryFetcher<
            IABSKUType extends IABSKU,
            IABProductDetailsType extends IABProductDetails<IABSKUType>>
        extends IABServiceConnector
        implements InventoryFetcher<IABSKUType, IABProductDetailsType, IABException>
{
    public static final String TAG = IABInventoryFetcher.class.getSimpleName();

    protected HashMap<IABSKUType, IABProductDetailsType> inventory;
    private List<IABSKUType> iabSKUs;

    private WeakReference<InventoryFetchedListener<IABSKUType, IABProductDetailsType, IABException>> inventoryListener = new WeakReference<>(null);
    @Inject protected Lazy<IABExceptionFactory> iabExceptionFactory;

    public IABInventoryFetcher(Context ctx)
    {
        super(ctx);
        this.inventory = new HashMap<>();
        DaggerUtils.inject(this);
    }

    public List<IABSKUType> getProductIdentifiers()
    {
        return iabSKUs;
    }

    public void setProductIdentifiers(List<IABSKUType> productIdentifiers)
    {
        this.iabSKUs = productIdentifiers;
    }

    abstract protected IABProductDetailsType createSKUDetails(String itemType, String json) throws JSONException;

    public void fetchInventory()
    {
        this.startConnectionSetup();
    }

    @Override protected void handleSetupFailed(IABException exception)
    {
        dispose();
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
                try
                {
                    return internalFetchCompleteInventory();
                }
                catch (RemoteException e)
                {
                    THLog.e(TAG, "Remote Exception while fetching inventory.", e);
                    exception = new IABRemoteException("RemoteException while fetching IAB", e);
                }
                catch (JSONException e)
                {
                    THLog.e(TAG, "Error parsing json.", e);
                    exception = new IABBadResponseException("Unable to parse JSON", e);
                }
                catch (IABException e)
                {
                    THLog.e(TAG, "IAB error.", e);
                    exception = e;
                }
                return null;
            }

            @Override protected void onPostExecute(HashMap<IABSKUType, IABProductDetailsType> skuskuDetailsMap)
            {
                if (exception != null)
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
        InventoryFetchedListener<IABSKUType, IABProductDetailsType, IABException> listenerCopy = getInventoryFetchedListener();
        if (listenerCopy != null)
        {
            listenerCopy.onInventoryFetchFail(iabSKUs, e);
        }
    }

    protected void notifyListenerFetched()
    {
        InventoryFetchedListener<IABSKUType, IABProductDetailsType, IABException> listenerCopy = getInventoryFetchedListener();
        if (listenerCopy != null)
        {
            listenerCopy.onInventoryFetchSuccess(iabSKUs, this.getInventory());
        }
    }

    protected HashMap<IABSKUType, IABProductDetailsType> internalFetchCompleteInventory() throws IABException, RemoteException, JSONException
    {
        if (iabSKUs == null || iabSKUs.isEmpty())
        {
            return new HashMap<>();
        }

        HashMap<IABSKUType, IABProductDetailsType> map = internalFetchSKUType(Constants.ITEM_TYPE_INAPP);

        if (areSubscriptionsSupported())
        {
            HashMap<IABSKUType, IABProductDetailsType> subscriptionsMap = internalFetchSKUType(Constants.ITEM_TYPE_SUBS);
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
        querySkus.putStringArrayList(Constants.GET_SKU_DETAILS_ITEM_LIST, identifiers);
        return querySkus;
    }

    private HashMap<IABSKUType, IABProductDetailsType> internalFetchSKUType(String itemType) throws IABException, RemoteException, JSONException
    {
        Bundle querySkus = getQuerySKUBundle();
        Bundle skuDetails = this.billingService.getSkuDetails(TARGET_BILLING_API_VERSION3, context.getPackageName(), itemType, querySkus);

        if (!skuDetails.containsKey(Constants.RESPONSE_GET_SKU_DETAILS_LIST))
        {
            int statusCode = Constants.getResponseCodeFromBundle(skuDetails);
            if (statusCode != Constants.BILLING_RESPONSE_RESULT_OK)
            {
                THLog.d(TAG, "getSkuDetails() failed: " + Constants.getStatusCodeDescription(statusCode));
                throw iabExceptionFactory.get().create(statusCode);
            }
            else
            {
                THLog.d(TAG, "getSkuDetails() returned a bundle with neither an error nor a detail list.");
                throw new IABBadResponseException(Constants.getStatusCodeDescription(statusCode));
            }
        }

        ArrayList<String> responseList = skuDetails.getStringArrayList(Constants.RESPONSE_GET_SKU_DETAILS_LIST);

        HashMap<IABSKUType, IABProductDetailsType> map = new HashMap<>();
        for (String json : responseList)
        {
            IABProductDetailsType details = createSKUDetails(itemType, json);
            THLog.d(TAG, "Got iabSKU details: " + details);
            map.put(details.getProductIdentifier(), details);
        }
        return map;
    }

    public Map<IABSKUType, IABProductDetailsType> getInventory()
    {
        return Collections.unmodifiableMap(inventory);
    }

    @Override public InventoryFetchedListener<IABSKUType, IABProductDetailsType, IABException> getInventoryFetchedListener()
    {
        return inventoryListener.get();
    }

    @Override public void setInventoryFetchedListener(InventoryFetchedListener<IABSKUType, IABProductDetailsType, IABException> inventoryFetchedListener)
    {
        this.inventoryListener = new WeakReference<>(inventoryFetchedListener);
    }
}
