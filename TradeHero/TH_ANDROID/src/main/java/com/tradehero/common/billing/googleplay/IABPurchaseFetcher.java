package com.tradehero.common.billing.googleplay;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.googleplay.exception.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.exception.IABVerificationFailedException;
import com.tradehero.th.base.Application;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.json.JSONException;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 3:31 PM To change this template use File | Settings | File Templates. */
abstract public class IABPurchaseFetcher<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends IABServiceConnector
    implements BillingPurchaseFetcher<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType,
        IABException>
{
    protected int requestCode;
    protected boolean fetching;
    protected Map<IABSKUType, IABPurchaseType> purchases;
    protected OnPurchaseFetchedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> fetchListener;
    @Inject protected Lazy<IABExceptionFactory> iabExceptionFactory;

    public IABPurchaseFetcher()
    {
        super();
        purchases = new HashMap<>();
    }

    @Override public void onDestroy()
    {
        fetchListener = null;
        super.onDestroy();
    }

    @Override public int getRequestCode()
    {
        return requestCode;
    }

    abstract protected IABPurchaseCache<IABSKUType, IABOrderIdType, IABPurchaseType> getPurchaseCache();

    @Override public void fetchPurchases(int requestCode)
    {
        checkNotFetching();
        this.requestCode = requestCode;
        this.fetching = true;
        startConnectionSetup();
    }

    protected void checkNotFetching()
    {
        if (fetching)
        {
            throw new IllegalStateException("Already fetching");
        }
    }

    @Override protected void handleSetupFailed(IABException exception)
    {
        super.handleSetupFailed(exception);
        notifyListenerFetchFailed(exception);
    }

    @Override protected void handleSetupFinished(IABResponse response)
    {
        super.handleSetupFinished(response);
        AsyncTask<Void, Void, HashMap<IABSKUType, IABPurchaseType>> backgroundTask = new AsyncTask<Void, Void, HashMap<IABSKUType, IABPurchaseType>>()
        {
            private Exception exception;
            @Override protected HashMap<IABSKUType, IABPurchaseType> doInBackground(Void... params)
            {
                if (!disposed)
                {
                    try
                    {
                        return queryPurchases();
                    }
                    catch (JSONException|RemoteException|IABException exception)
                    {
                        Timber.e("Failed querying purchases", exception);
                        exception.printStackTrace();
                        this.exception = exception;
                    }
                }
                return null;
            }

            @Override protected void onPostExecute(HashMap<IABSKUType, IABPurchaseType> skuGooglePurchaseHashMap)
            {
                if (disposed)
                {
                    // Nothing to do
                }
                else if (exception != null)
                {
                    Timber.e("Failed querying purchases", exception);
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

    protected HashMap<IABSKUType, IABPurchaseType> queryPurchases() throws JSONException, RemoteException, IABException
    {
        HashMap<IABSKUType, IABPurchaseType> map = queryPurchases(IABConstants.ITEM_TYPE_INAPP);
        if (areSubscriptionsSupported())
        {
            HashMap<IABSKUType, IABPurchaseType> subscriptionMap = queryPurchases(IABConstants.ITEM_TYPE_SUBS);
            map.putAll(subscriptionMap);
        }
        getPurchaseCache().put(map);
        return map;
    }

    protected HashMap<IABSKUType, IABPurchaseType> queryPurchases(String itemType) throws JSONException, RemoteException, IABException
    {
        // Query purchase
        Timber.d("Querying owned items, item type: %s", itemType);
        Timber.d("Package name: %s", Application.context().getPackageName());
        String continueToken = null;
        HashMap<IABSKUType, IABPurchaseType> purchasesMap = new HashMap<>();

        do
        {
            Bundle ownedItems = getPurchases(itemType, continueToken);

            int response = IABConstants.getResponseCodeFromBundle(ownedItems);
            Timber.d("Owned items response: %s", String.valueOf(response));
            if (response != IABConstants.BILLING_RESPONSE_RESULT_OK)
            {
                throw iabExceptionFactory.get().create(response);
            }
            if (!ownedItems.containsKey(IABConstants.RESPONSE_INAPP_ITEM_LIST)
                    || !ownedItems.containsKey(IABConstants.RESPONSE_INAPP_PURCHASE_DATA_LIST)
                    || !ownedItems.containsKey(IABConstants.RESPONSE_INAPP_SIGNATURE_LIST))
            {
                throw new IABBadResponseException("Bundle returned from getPurchases() doesn't contain required fields.");
            }

            ArrayList<String> ownedSkus = ownedItems.getStringArrayList(IABConstants.RESPONSE_INAPP_ITEM_LIST);
            ArrayList<String> purchaseDataList = ownedItems.getStringArrayList(IABConstants.RESPONSE_INAPP_PURCHASE_DATA_LIST);
            ArrayList<String> signatureList = ownedItems.getStringArrayList(IABConstants.RESPONSE_INAPP_SIGNATURE_LIST);

            for (int i = 0; i < purchaseDataList.size(); ++i)
            {
                String purchaseData = purchaseDataList.get(i);
                String signature = signatureList.get(i);
                String sku = ownedSkus.get(i);
                if (Security.verifyPurchase(IABConstants.BASE_64_PUBLIC_KEY, purchaseData, signature))
                {
                    Timber.d("Sku is owned: %s", sku);
                    IABPurchaseType purchase = createPurchase(itemType, purchaseData, signature);

                    if (TextUtils.isEmpty(purchase.getToken()))
                    {
                        Timber.w("BUG: empty/null token!");
                        Timber.d("Purchase data: %s", purchaseData);
                    }

                    // Record ownership and token
                    purchasesMap.put(purchase.getProductIdentifier(), purchase);
                }
                else
                {
                    throw new IABVerificationFailedException("Purchase signature verification **FAILED**. Not adding item. Purchase data: " + purchaseData + "   Signature: " + signature);
                }
            }

            continueToken = ownedItems.getString(IABConstants.INAPP_CONTINUATION_TOKEN);
            Timber.d("Continuation token: %s", continueToken);
        }
        while (!TextUtils.isEmpty(continueToken));
        return purchasesMap;
    }

    protected Bundle getPurchases(String itemType, String continueToken) throws RemoteException
    {
        Timber.d("Calling getPurchases with continuation token: %s", continueToken);
        return billingService.getPurchases(TARGET_BILLING_API_VERSION3, context.getPackageName(), itemType, continueToken);
    }

    abstract protected IABPurchaseType createPurchase(String itemType, String purchaseData, String signature) throws JSONException;

    @Override public OnPurchaseFetchedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> getFetchListener()
    {
        return fetchListener;
    }

    @Override public void setPurchaseFetchedListener(
            OnPurchaseFetchedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> fetchListener)
    {
        this.fetchListener = fetchListener;
    }

    protected void notifyListenerFetched()
    {
        OnPurchaseFetchedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> listenerCopy = getFetchListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchedPurchases(requestCode, this.purchases);
        }
    }

    protected void notifyListenerFetchFailed(IABException exception)
    {
        OnPurchaseFetchedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> listenerCopy = getFetchListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchPurchasesFailed(requestCode, exception);
        }
    }
}
