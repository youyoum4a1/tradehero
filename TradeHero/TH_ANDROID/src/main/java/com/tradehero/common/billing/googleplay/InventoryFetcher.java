package com.tradehero.common.billing.googleplay;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.utils.THLog;
import org.json.JSONException;

import java.util.*;

/**
 * Created by julien on 4/11/13
 */
public class InventoryFetcher extends IABServiceConnector
{
    public static final String TAG = InventoryFetcher.class.getSimpleName();

    protected HashMap<SKU, SKUDetails> inventory;
    private List<SKU> skus;

    private InventoryListener inventoryListener;

    public InventoryFetcher(Context ctx, List<SKU> skus)
    {
       super(ctx);
       this.skus = skus;
       this.inventory = new HashMap<>(skus != null ? skus.size() : 10);
    }

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
        //TODO: do it in a backgorund thread
        try
        {
            _internalFetchInventory();
            if (inventoryListener != null)
                inventoryListener.onInventoryFetchSuccess(this, this.getInventory());
            dispose();
        }
        catch (RemoteException e)
        {
            THLog.e(TAG, "Remote Exception while fetching inventory.", e);
            handleInventoryFetchFailure(new IABException(Constants.IABHELPER_REMOTE_EXCEPTION, "RemoteException while fetching IAB", e));
        } catch (JSONException e)
        {
            THLog.e(TAG, "Error parsing json.", e);
            handleInventoryFetchFailure(new IABException(Constants.IABHELPER_BAD_RESPONSE, "Unable to parse JSON", e));
        } catch (IABException e)
        {
            THLog.e(TAG, "IAB error.", e);
            handleInventoryFetchFailure(e);
        }
    }

    private void handleInventoryFetchFailure(IABException e)
    {
        if (this.inventoryListener != null)
            this.inventoryListener.onInventoryFetchFail(this, e);
    }

    private void _internalFetchInventory() throws IABException, RemoteException, JSONException
    {
        if (skus == null || skus.isEmpty())
            return;

        _internalFetchSKU(Constants.ITEM_TYPE_INAPP);

        if (areSubscriptionsSupported()) {
            _internalFetchSKU(Constants.ITEM_TYPE_SUBS);
        }
    }

    private Bundle getQuerySKUBundle()
    {
        ArrayList<String> identifiers = new ArrayList<>(this.skus.size());
        for (SKU sku : this.skus)
        {
            identifiers.add(sku.identifier);
        }
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList(Constants.GET_SKU_DETAILS_ITEM_LIST, identifiers);
        return querySkus;
    }


    private void _internalFetchSKU(String itemType) throws IABException, RemoteException, JSONException
    {
        Bundle querySkus = getQuerySKUBundle();
        Bundle skuDetails = this.billingService.getSkuDetails(TARGET_BILLING_API_VERSION3, context.getPackageName(),itemType, querySkus);

        if (!skuDetails.containsKey(Constants.RESPONSE_GET_SKU_DETAILS_LIST)) {
            int statusCode = getResponseCodeFromBundle(skuDetails);
            if (statusCode != Constants.BILLING_RESPONSE_RESULT_OK) {
                THLog.d(TAG, "getSkuDetails() failed: " + Constants.getStatusCodeDescription(statusCode));
                throw new IABException(statusCode, Constants.getStatusCodeDescription(statusCode));
            }
            else {
                THLog.d(TAG, "getSkuDetails() returned a bundle with neither an error nor a detail list.");
                throw new IABException(Constants.IABHELPER_BAD_RESPONSE, Constants.getStatusCodeDescription(statusCode));
            }
        }

        ArrayList<String> responseList = skuDetails.getStringArrayList(Constants.RESPONSE_GET_SKU_DETAILS_LIST);

        for (String json : responseList) {
            SKUDetails d = new SKUDetails(itemType, json);
            THLog.d(TAG, "Got sku details: " + d);
            this.inventory.put(d.sku, d);
        }
    }

    public Map<SKU, SKUDetails> getInventory()
    {
        return Collections.unmodifiableMap(inventory);
    }

    public InventoryListener getInventoryListener()
    {
        return inventoryListener;
    }

    public void setInventoryListener(InventoryListener inventoryListener)
    {
        this.inventoryListener = inventoryListener;
    }

    public static interface InventoryListener
    {
        void onInventoryFetchSuccess(InventoryFetcher fetcher, Map<SKU, SKUDetails> inventory);
        void onInventoryFetchFail(InventoryFetcher fetcher, IABException exception);
    }
}
