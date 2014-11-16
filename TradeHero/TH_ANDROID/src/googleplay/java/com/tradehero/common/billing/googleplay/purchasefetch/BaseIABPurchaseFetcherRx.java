package com.tradehero.common.billing.googleplay.purchasefetch;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.tradehero.common.billing.googleplay.BaseIABServiceCaller;
import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABServiceResult;
import com.tradehero.common.billing.googleplay.Security;
import com.tradehero.common.billing.googleplay.exception.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.exception.IABVerificationFailedException;
import com.tradehero.common.billing.purchasefetch.PurchaseFetchResult;
import com.tradehero.th.BuildConfig;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import rx.Observable;
import timber.log.Timber;

abstract public class BaseIABPurchaseFetcherRx<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends BaseIABServiceCaller<PurchaseFetchResult<IABSKUType,
        IABOrderIdType,
        IABPurchaseType>>
        implements IABPurchaseFetcherRx<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType>
{
    protected boolean fetching;
    protected List<IABPurchaseType> purchases;

    //<editor-fold desc="Constructors">
    public BaseIABPurchaseFetcherRx(
            int requestCode,
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        super(requestCode, context, iabExceptionFactory);
        purchases = new ArrayList<>();
        fetchPurchasesAndTellSubject();
    }
    //</editor-fold>

    @NonNull @Override public Observable<PurchaseFetchResult<IABSKUType, IABOrderIdType, IABPurchaseType>> get()
    {
        return replayObservable;
    }

    protected void fetchPurchasesAndTellSubject()
    {
        getBillingServiceResult()
                .flatMap(this::fetchPurchases)
                .map(this::createPurchaseResult)
                .subscribe(subject);
    }

    protected Observable<IABPurchaseType> fetchPurchases(@NonNull IABServiceResult serviceResult)
    {
        Observable<IABPurchaseType> purchases = fetchPurchases(serviceResult, IABConstants.ITEM_TYPE_INAPP);
        if (serviceResult.subscriptionSupported)
        {
            purchases.mergeWith(fetchPurchases(serviceResult, IABConstants.ITEM_TYPE_SUBS));
        }
        return purchases;
    }

    protected Observable<IABPurchaseType> fetchPurchases(
            @NonNull IABServiceResult serviceResult,
            @NonNull String itemType)
    {
        // Query purchase
        Timber.d("Querying owned items, item type: %s", itemType);
        Observable<IABPurchaseType> purchases = Observable.empty();
        String continueToken = null;
        try
        {
            do
            {
                Bundle ownedItems = getPurchasesBundle(serviceResult, itemType, continueToken);
                if (ownedItems != null)
                {
                    int response = IABConstants.getResponseCodeFromBundle(ownedItems);
                    Timber.d("Owned items response: %s", String.valueOf(response));
                    if (response != IABConstants.BILLING_RESPONSE_RESULT_OK)
                    {
                        throw iabExceptionFactory.create(response);
                    }

                    purchases.mergeWith(fetchPurchases(ownedItems, itemType));

                    continueToken = ownedItems.getString(IABConstants.INAPP_CONTINUATION_TOKEN);
                    Timber.d("Continuation token: %s", continueToken);
                }
            }
            while (!TextUtils.isEmpty(continueToken));
        } catch (Exception e)
        {
            return Observable.error(e);
        }
        return purchases;
    }

    private Observable<IABPurchaseType> fetchPurchases(@NonNull Bundle ownedItems, @NonNull String itemType) throws JSONException
    {
        if (!ownedItems.containsKey(IABConstants.RESPONSE_INAPP_ITEM_LIST)
                || !ownedItems.containsKey(IABConstants.RESPONSE_INAPP_PURCHASE_DATA_LIST)
                || !ownedItems.containsKey(IABConstants.RESPONSE_INAPP_SIGNATURE_LIST))
        {
            return Observable.error(new IABBadResponseException("Bundle returned from getPurchases() doesn't contain required fields."));
        }

        ArrayList<String> ownedSkus = ownedItems.getStringArrayList(IABConstants.RESPONSE_INAPP_ITEM_LIST);
        ArrayList<String> purchaseDataList = ownedItems.getStringArrayList(IABConstants.RESPONSE_INAPP_PURCHASE_DATA_LIST);
        ArrayList<String> signatureList = ownedItems.getStringArrayList(IABConstants.RESPONSE_INAPP_SIGNATURE_LIST);
        List<IABPurchaseType> purchases = new ArrayList<>();

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
                purchases.add(purchase);
            }
            else
            {
                return Observable.error(
                        new IABVerificationFailedException("Purchase signature verification **FAILED**. Not adding item. Purchase data: "
                                + purchaseData
                                + "   Signature: "
                                + signature));
            }
        }
        return Observable.from(purchases);
    }

    protected Bundle getPurchasesBundle(@NonNull IABServiceResult serviceResult, @NonNull String itemType, @Nullable String continueToken)
            throws RemoteException
    {
        Timber.d("Calling getPurchases with continuation token: %s", continueToken);
        return serviceResult.billingService.getPurchases(
                TARGET_BILLING_API_VERSION3,
                BuildConfig.GOOGLE_PLAY_PACKAGE_NAME,
                itemType,
                continueToken);
    }

    @NonNull protected PurchaseFetchResult<IABSKUType, IABOrderIdType, IABPurchaseType> createPurchaseResult(
            @NonNull IABPurchaseType purchase)
    {
        return new PurchaseFetchResult<>(getRequestCode(), purchase);
    }

    @NonNull protected PurchaseFetchResult<IABSKUType, IABOrderIdType, IABPurchaseType> createPurchaseResult(
            @NonNull String itemType,
            @NonNull String purchaseData,
            @NonNull String signature)
            throws JSONException
    {
        return new PurchaseFetchResult<>(getRequestCode(), createPurchase(itemType, purchaseData, signature));
    }

    @NonNull abstract protected IABPurchaseType createPurchase(
            @NonNull String itemType,
            @NonNull String purchaseData,
            @NonNull String signature)
            throws JSONException;
}
