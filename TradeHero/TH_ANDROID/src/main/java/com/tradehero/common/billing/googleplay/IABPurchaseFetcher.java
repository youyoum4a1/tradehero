package com.tradehero.common.billing.googleplay;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import com.android.vending.billing.IInAppBillingService;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.googleplay.exception.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.exception.IABVerificationFailedException;
import com.tradehero.th.base.Application;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.json.JSONException;
import timber.log.Timber;

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
    protected List<IABPurchaseType> purchases;
    protected OnPurchaseFetchedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> fetchListener;
    @Inject protected Lazy<IABExceptionFactory> iabExceptionFactory;

    public IABPurchaseFetcher()
    {
        super();
        purchases = new ArrayList<>();
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
        AsyncTask<Void, Void, ArrayList<IABPurchaseType>> backgroundTask = new AsyncTask<Void, Void, ArrayList<IABPurchaseType>>()
        {
            private Exception exception;
            @Override protected ArrayList<IABPurchaseType> doInBackground(Void... params)
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

            @Override protected void onPostExecute(ArrayList<IABPurchaseType> skuGooglePurchaseHashMap)
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

    protected ArrayList<IABPurchaseType> queryPurchases() throws JSONException, RemoteException, IABException
    {
        ArrayList<IABPurchaseType> list = queryPurchases(IABConstants.ITEM_TYPE_INAPP);
        if (areSubscriptionsSupported())
        {
            ArrayList<IABPurchaseType> subscriptionAll = queryPurchases(IABConstants.ITEM_TYPE_SUBS);
            list.addAll(subscriptionAll);
        }
        getPurchaseCache().put(list);
        return list;
    }

    protected ArrayList<IABPurchaseType> queryPurchases(String itemType) throws JSONException, RemoteException, IABException
    {
        // Query purchase
        Timber.d("Querying owned items, item type: %s", itemType);
        Timber.d("Package name: %s", Application.context().getPackageName());
        String continueToken = null;
        ArrayList<IABPurchaseType> purchasesList = new ArrayList<>();

        do
        {
            Bundle ownedItems = getPurchases(itemType, continueToken);

            if (ownedItems == null)
            {
                return purchasesList;
            }

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
                    purchasesList.add(purchase);
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
        return purchasesList;
    }

    protected Bundle getPurchases(String itemType, String continueToken) throws RemoteException
    {
        Timber.d("Calling getPurchases with continuation token: %s", continueToken);
        IInAppBillingService billingServiceCopy = billingService;
        Activity currentActivity = currentActivityHolder.getCurrentActivity();
        if (currentActivity == null || billingServiceCopy == null)
        {
            return null;
        }

        return billingServiceCopy.getPurchases(
                TARGET_BILLING_API_VERSION3,
                currentActivity.getPackageName(),
                itemType,
                continueToken);
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
