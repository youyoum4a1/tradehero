package com.tradehero.common.billing.googleplay.purchase;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABServiceCaller;
import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaseOrder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABServiceResult;
import com.tradehero.common.billing.googleplay.Security;
import com.tradehero.common.billing.googleplay.exception.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.exception.IABSubscriptionUnavailableException;
import com.tradehero.common.billing.googleplay.exception.IABUnknownErrorException;
import com.tradehero.common.billing.googleplay.exception.IABVerificationFailedException;
import com.tradehero.common.billing.purchase.PurchaseResult;
import com.ayondo.academy.BuildConfig;
import org.json.JSONException;
import rx.Observable;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

abstract public class BaseIABPurchaserRx<
        IABSKUType extends IABSKU,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends BaseIABServiceCaller
        implements IABPurchaserRx<
        IABSKUType,
        IABPurchaseOrderType,
        IABOrderIdType,
        IABPurchaseType>
{
    @NonNull protected final IABPurchaseOrderType purchaseOrder;
    protected BehaviorSubject<PurchaseResult<
            IABSKUType,
            IABPurchaseOrderType,
            IABOrderIdType,
            IABPurchaseType>> subject;

    //<editor-fold desc="Constructors">
    public BaseIABPurchaserRx(
            int requestCode,
            @NonNull IABPurchaseOrderType purchaseOrder,
            @NonNull Activity activity,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        super(requestCode, activity, iabExceptionFactory);
        this.purchaseOrder = purchaseOrder;
        subject = BehaviorSubject.create();
    }
    //</editor-fold>

    @NonNull @Override public IABPurchaseOrderType getPurchaseOrder()
    {
        return purchaseOrder;
    }

    @NonNull @Override public Observable<PurchaseResult<IABSKUType,
            IABPurchaseOrderType,
            IABOrderIdType,
            IABPurchaseType>> get()
    {
        getBillingServiceResult().subscribe(
                new Action1<IABServiceResult>()
                {
                    @Override public void call(IABServiceResult result)
                    {
                        BaseIABPurchaserRx.this.startPurchaseActivity(result);
                    }
                },
                new Action1<Throwable>()
                {
                    @Override public void call(Throwable error)
                    {
                        subject.onError(error);
                    }
                });
        return subject.asObservable();
    }

    private void startPurchaseActivity(@NonNull IABServiceResult serviceResult)
    {
        if (!serviceResult.subscriptionSupported && purchaseOrder.getType().equals(IABConstants.ITEM_TYPE_SUBS))
        {
            subject.onError(new IABSubscriptionUnavailableException("Subscriptions are not available."));
        }
        else
        {
            try
            {
                Bundle buyIntentBundle = createBuyIntentBundle(serviceResult);
                PendingIntent pendingIntent = buyIntentBundle.getParcelable(IABConstants.RESPONSE_BUY_INTENT);
                ((Activity) context).startIntentSenderForResult(
                        pendingIntent.getIntentSender(),
                        getRequestCode(), new Intent(), 0, 0, 0);
                // We need to wait for the return from the activity.
            } catch (Throwable e)
            {
                Timber.e(e, "Failed sending purchase intent for %s", purchaseOrder);
                subject.onError(e);
            }
        }
    }

    @NonNull private Bundle createBuyIntentBundle(@NonNull IABServiceResult serviceResult) throws RemoteException, IABException
    {
        Bundle buyIntentBundle = serviceResult.billingService.getBuyIntent(
                TARGET_BILLING_API_VERSION3,
                BuildConfig.GOOGLE_PLAY_PACKAGE_NAME,
                purchaseOrder.getProductIdentifier().identifier,
                purchaseOrder.getType(),
                purchaseOrder.getDeveloperPayload());
        int response = IABConstants.getResponseCodeFromBundle(buyIntentBundle);
        if (response != IABConstants.BILLING_RESPONSE_RESULT_OK)
        {
            Timber.d("Unable to buy item, Error response: %s", IABConstants.getStatusCodeDescription(
                    response));
            String message = String.format("id %s, type %s, payload %s",
                    purchaseOrder.getProductIdentifier().identifier,
                    purchaseOrder.getType(),
                    purchaseOrder.getDeveloperPayload());
            throw iabExceptionFactory.create(response, message);
        }
        return buyIntentBundle;
    }

    /**
     * Handles an activity result that's part of the purchase flow in in-app billing. If you are calling {@link #get()}, then you must call this
     * method from your Activity's {@link android.app.Activity@onActivityResult} method. This method MUST be called from the UI thread of the
     * Activity.
     *
     *
     * @param activity
     * @param requestCode The requestCode as you received it.
     * @param resultCode The resultCode as you received it.
     * @param data The data (Intent) as you received it.
     * @return Returns true if the result was related to a purchase flow and was handled; false if the result was not related to a purchase, in which
     * case you should handle it normally.
     */
    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        Timber.d("handleActivityResult requestCode: %d, resultCode: %d", requestCode, resultCode);
        if (requestCode != getRequestCode())
        {
            Timber.w("handleActivityResult. This requestcode was not for me");
        }
        else
        {
            if (data == null)
            {
                Timber.w("Null data in IAB activity result.");
                subject.onError(new IABBadResponseException("Null data in IAB result"));
            }
            else
            {
                int responseCode = IABConstants.getResponseCodeFromIntent(data);
                String purchaseData = data.getStringExtra(IABConstants.RESPONSE_INAPP_PURCHASE_DATA);
                String dataSignature = data.getStringExtra(IABConstants.RESPONSE_INAPP_SIGNATURE);

                if (resultCode == Activity.RESULT_OK && responseCode == IABConstants.BILLING_RESPONSE_RESULT_OK)
                {
                    Timber.d("Successful resultCode from purchase activity.");
                    Timber.d("Purchase data: %s" + purchaseData);
                    Timber.d("Data signature: %s" + dataSignature);
                    Timber.d("Extras: %s" + data.getExtras());
                    Timber.d("Expected item type: %s" + purchaseOrder.getType());

                    if (purchaseData == null || dataSignature == null)
                    {
                        Timber.w("BUG: either purchaseData or dataSignature is null.");
                        Timber.w("Extras: %s" + data.getExtras().toString());
                        subject.onError(new IABUnknownErrorException("IAB returned null purchaseData or dataSignature"));
                    }
                    else
                    {
                        try
                        {
                            IABPurchaseType purchase = createPurchase(purchaseData, dataSignature);
                            String sku = purchase.getProductIdentifier().identifier;

                            // Verify signature
                            if (!Security.verifyPurchase(IABConstants.BASE_64_PUBLIC_KEY, purchaseData, dataSignature))
                            {
                                Timber.w("Purchase signature verification FAILED for sku " + sku);
                                subject.onError(new IABVerificationFailedException("Signature verification failed for sku " + sku));
                            }
                            else
                            {
                                Timber.d("Purchase signature successfully verified.");
                                subject.onNext(new PurchaseResult<>(requestCode, purchaseOrder, purchase));
                                subject.onCompleted();
                            }
                        } catch (JSONException e)
                        {
                            Timber.e("Failed to parse purchase data.", e);
                            subject.onError(new IABBadResponseException("Failed to parse purchase data."));
                        }
                    }
                }
                else if (resultCode == Activity.RESULT_OK)
                {
                    // result code was OK, but in-app billing response was not OK.
                    Timber.w("Result code was OK but in-app billing response was not OK: %s",
                            IABConstants.getStatusCodeDescription(responseCode));
                    subject.onError(iabExceptionFactory.create(responseCode));
                }
                else if (resultCode == Activity.RESULT_CANCELED)
                {
                    Timber.w("Purchase canceled - Response: %s",
                            IABConstants.getStatusCodeDescription(responseCode));
                    subject.onError(iabExceptionFactory.create(IABConstants.IABHELPER_USER_CANCELLED));
                }
                else
                {
                    Timber.w(
                            "Purchase failed. Result code: %d. Response: %s",
                            resultCode,
                            IABConstants.getStatusCodeDescription(responseCode));
                    subject.onError(iabExceptionFactory.create(IABConstants.IABHELPER_UNKNOWN_PURCHASE_RESPONSE));
                }
            }
        }
    }

    @NonNull protected IABPurchaseType createPurchase(
            @NonNull String purchaseData,
            @NonNull String dataSignature) throws JSONException
    {
        return createPurchase(purchaseOrder.getType(), purchaseData, dataSignature);
    }

    @NonNull abstract protected IABPurchaseType createPurchase(
            @NonNull String itemType,
            @NonNull String purchaseData,
            @NonNull String dataSignature) throws JSONException;
}
