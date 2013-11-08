package com.tradehero.common.billing.googleplay;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import com.tradehero.common.billing.ProductDetailsTuner;
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
abstract public class InventoryFetcher<SKUDetailsType extends SKUDetails>
        extends IABServiceConnector
{
    public static final String TAG = InventoryFetcher.class.getSimpleName();

    protected HashMap<IABSKU, SKUDetailsType> inventory;
    private List<IABSKU> iabSKUs;

    private WeakReference<InventoryListener> inventoryListener = new WeakReference<>(null);
    private WeakReference<ProductDetailsTuner<SKUDetailsType>> skuDetailsTuner = new WeakReference<>(null);
    @Inject protected Lazy<IABExceptionFactory> iabExceptionFactory;

    public InventoryFetcher(Context ctx, List<IABSKU> iabSKUs)
    {
        super(ctx);
        this.iabSKUs = iabSKUs;
        this.inventory = new HashMap<>(iabSKUs != null ? iabSKUs.size() : 10);
        DaggerUtils.inject(this);
    }

    abstract protected SKUDetailsType createSKUDetails(String itemType, String json) throws JSONException;

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
        AsyncTask<Void, Void, HashMap<IABSKU, SKUDetailsType>> backgroundTask =  new AsyncTask<Void, Void, HashMap<IABSKU, SKUDetailsType>>() {
            private IABException exception;

            @Override protected HashMap<IABSKU, SKUDetailsType> doInBackground(Void... params)
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

            @Override protected void onPostExecute(HashMap<IABSKU, SKUDetailsType> skuskuDetailsMap)
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
        InventoryListener listenerCopy = getInventoryListener();
        if (listenerCopy != null)
        {
            listenerCopy.onInventoryFetchFail(this, e);
        }
    }

    protected void notifyListenerFetched()
    {
        InventoryListener listenerCopy = getInventoryListener();
        if (listenerCopy != null)
        {
            listenerCopy.onInventoryFetchSuccess(this, this.getInventory());
        }
    }

    private HashMap<IABSKU, SKUDetailsType> internalFetchCompleteInventory() throws IABException, RemoteException, JSONException
    {
        if (iabSKUs == null || iabSKUs.isEmpty())
        {
            return new HashMap<>();
        }

        HashMap<IABSKU, SKUDetailsType> map = internalFetchSKUType(Constants.ITEM_TYPE_INAPP);

        if (areSubscriptionsSupported())
        {
            HashMap<IABSKU, SKUDetailsType> subscriptionsMap = internalFetchSKUType(Constants.ITEM_TYPE_SUBS);
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

    private HashMap<IABSKU, SKUDetailsType> internalFetchSKUType(String itemType) throws IABException, RemoteException, JSONException
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

        HashMap<IABSKU, SKUDetailsType> map = new HashMap<>();
        for (String json : responseList)
        {
            SKUDetailsType details = createSKUDetails(itemType, json);
            fineTune(details);
            THLog.d(TAG, "Got iabSKU details: " + details);
            map.put(details.iabSKU, details);
        }
        return map;
    }

    public Map<IABSKU, SKUDetailsType> getInventory()
    {
        return Collections.unmodifiableMap(inventory);
    }

    public InventoryListener getInventoryListener()
    {
        return inventoryListener.get();
    }

    public void setInventoryListener(InventoryListener inventoryListener)
    {
        this.inventoryListener = new WeakReference<>(inventoryListener);
    }

    public ProductDetailsTuner<SKUDetailsType> getSkuDetailsTuner()
    {
        return skuDetailsTuner.get();
    }

    public void setSkuDetailsTuner(ProductDetailsTuner<SKUDetailsType> skuDetailsTuner)
    {
        this.skuDetailsTuner = new WeakReference<>(skuDetailsTuner);
    }

    private void fineTune(SKUDetailsType skuDetails)
    {
        ProductDetailsTuner<SKUDetailsType> tuner = getSkuDetailsTuner();
        if (tuner != null)
        {
            tuner.fineTune(skuDetails);
        }
    }

    public static interface InventoryListener<InventoryFetcherType extends InventoryFetcher, SKUDetailsType extends SKUDetails>
    {
        void onInventoryFetchSuccess(InventoryFetcherType fetcher, Map<IABSKU, SKUDetailsType> inventory);
        void onInventoryFetchFail(InventoryFetcherType fetcher, IABException exception);
    }
}
