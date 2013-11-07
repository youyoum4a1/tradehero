package com.tradehero.common.billing.googleplay;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import com.tradehero.common.billing.googleplay.exceptions.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.billing.googleplay.exceptions.IABVerificationFailedException;
import com.tradehero.common.utils.THLog;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 3:31 PM To change this template use File | Settings | File Templates. */
public class PurchaseFetcher extends IABServiceConnector
{
    public static final String TAG = PurchaseFetcher.class.getSimpleName();

    private Map<SKU, IABPurchase> purchases;

    protected WeakReference<PublicFetcherListener> fetchListener = new WeakReference<>(null);

    public PurchaseFetcher(Context ctx)
    {
        super(ctx);
        purchases = new HashMap<>();
    }

    @Override protected void handleSetupFailed(IABException exception)
    {
        super.handleSetupFailed(exception);
        dispose();
        notifyListenerFetchFailed(exception);
    }

    @Override protected void handleSetupFinished(IABResponse response)
    {
        super.handleSetupFinished(response);
        AsyncTask<Void, Void, HashMap<SKU, IABPurchase>> backgroundTask = new AsyncTask<Void, Void, HashMap<SKU, IABPurchase>>()
        {
            private Exception exception;
            @Override protected HashMap<SKU, IABPurchase> doInBackground(Void... params)
            {
                try
                {
                    HashMap<SKU, IABPurchase> map = queryPurchases(Constants.ITEM_TYPE_INAPP);
                    if (areSubscriptionsSupported())
                    {
                        HashMap<SKU, IABPurchase> subscriptionMap = queryPurchases(Constants.ITEM_TYPE_SUBS);
                        map.putAll(subscriptionMap);
                    }
                    return map;
                }
                catch (JSONException|RemoteException|IABException exception)
                {
                    THLog.e(TAG, "Failed querying purchases", exception);
                    exception.printStackTrace();
                }
                return null;
            }

            @Override protected void onPostExecute(HashMap<SKU, IABPurchase> skuGooglePurchaseHashMap)
            {
                if (exception != null)
                {
                    THLog.e(TAG, "Failed querying purchases", exception);
                    exception.printStackTrace();
                }
                else
                {
                    purchases = skuGooglePurchaseHashMap;
                    notifyListenerFetched();
                }
            }
        };
        backgroundTask.execute();
    }

    protected HashMap<SKU, IABPurchase> queryPurchases(String itemType) throws JSONException, RemoteException, IABException
    {
        // Query purchases
        THLog.d(TAG, "Querying owned items, item type: " + itemType);
        THLog.d(TAG, "Package name: " + context.getPackageName());
        String continueToken = null;
        HashMap<SKU, IABPurchase> purchasesMap = new HashMap<>();

        do
        {
            Bundle ownedItems = getPurchases(itemType, continueToken);

            int response = Constants.getResponseCodeFromBundle(ownedItems);
            THLog.d(TAG, "Owned items response: " + String.valueOf(response));
            if (response != Constants.BILLING_RESPONSE_RESULT_OK)
            {
                throw new IABException(response, "getPurchases() failed: " + Constants.getStatusCodeDescription(response));
            }
            if (!ownedItems.containsKey(Constants.RESPONSE_INAPP_ITEM_LIST)
                    || !ownedItems.containsKey(Constants.RESPONSE_INAPP_PURCHASE_DATA_LIST)
                    || !ownedItems.containsKey(Constants.RESPONSE_INAPP_SIGNATURE_LIST))
            {
                throw new IABBadResponseException("Bundle returned from getPurchases() doesn't contain required fields.");
            }

            ArrayList<String> ownedSkus = ownedItems.getStringArrayList(Constants.RESPONSE_INAPP_ITEM_LIST);
            ArrayList<String> purchaseDataList = ownedItems.getStringArrayList(Constants.RESPONSE_INAPP_PURCHASE_DATA_LIST);
            ArrayList<String> signatureList = ownedItems.getStringArrayList(Constants.RESPONSE_INAPP_SIGNATURE_LIST);

            for (int i = 0; i < purchaseDataList.size(); ++i)
            {
                String purchaseData = purchaseDataList.get(i);
                String signature = signatureList.get(i);
                String sku = ownedSkus.get(i);
                if (Security.verifyPurchase(Constants.BASE_64_PUBLIC_KEY, purchaseData, signature))
                {
                    THLog.d(TAG, "Sku is owned: " + sku);
                    IABPurchase purchase = new IABPurchase(itemType, purchaseData, signature);

                    if (TextUtils.isEmpty(purchase.token))
                    {
                        THLog.w(TAG, "BUG: empty/null token!");
                        THLog.d(TAG, "Purchase data: " + purchaseData);
                    }

                    // Record ownership and token
                    purchasesMap.put(purchase.getProductIdentifier(), purchase);
                }
                else
                {
                    throw new IABVerificationFailedException("Purchase signature verification **FAILED**. Not adding item. Purchase data: " + purchaseData + "   Signature: " + signature);
                }
            }

            continueToken = ownedItems.getString(Constants.INAPP_CONTINUATION_TOKEN);
            THLog.d(TAG, "Continuation token: " + continueToken);
        }
        while (!TextUtils.isEmpty(continueToken));
        return purchasesMap;
    }

    protected Bundle getPurchases(String itemType, String continueToken) throws RemoteException
    {
        THLog.d(TAG, "Calling getPurchases with continuation token: " + continueToken);
        return billingService.getPurchases(TARGET_BILLING_API_VERSION3, context.getPackageName(), itemType, continueToken);
    }

    public PublicFetcherListener getFetchListener()
    {
        return fetchListener.get();
    }

    public void setFetchListener(PublicFetcherListener fetchListener)
    {
        this.fetchListener = new WeakReference<PublicFetcherListener>(fetchListener);
    }

    protected void notifyListenerFetched()
    {
        PublicFetcherListener listenerCopy = getFetchListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchedPurchases(this, this.purchases);
        }
    }

    protected void notifyListenerFetchFailed(IABException exception)
    {
        PublicFetcherListener listenerCopy = getFetchListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchPurchasesFailed(this, exception);
        }
    }

    public static interface PublicFetcherListener
    {
        void onFetchedPurchases(PurchaseFetcher fetcher, Map<SKU, IABPurchase> purchases);
        void onFetchPurchasesFailed(PurchaseFetcher fetcher, IABException exception);
    }
}
