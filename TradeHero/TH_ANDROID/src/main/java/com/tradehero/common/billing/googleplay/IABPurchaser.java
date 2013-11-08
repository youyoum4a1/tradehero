package com.tradehero.common.billing.googleplay;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.RemoteException;
import com.tradehero.common.billing.googleplay.exceptions.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.billing.googleplay.exceptions.IABRemoteException;
import com.tradehero.common.billing.googleplay.exceptions.IABSendIntentException;
import com.tradehero.common.billing.googleplay.exceptions.IABSubscriptionUnavailableException;
import com.tradehero.common.billing.googleplay.exceptions.IABUnknownErrorException;
import com.tradehero.common.billing.googleplay.exceptions.IABVerificationFailedException;
import com.tradehero.common.utils.THLog;
import java.lang.ref.WeakReference;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/7/13 Time: 11:05 AM To change this template use File | Settings | File Templates. */
public class IABPurchaser<SKUDetailsType extends SKUDetails> extends IABServiceConnector
{
    public static final String TAG = IABPurchaser.class.getSimpleName();

    private boolean purchasing = false;
    private SKUDetailsType skuDetails;
    private String extraData;
    private int activityRequestCode;
    private WeakReference<OnIABPurchaseFinishedListener> purchaseFinishedListener = new WeakReference<>(null);

    public IABPurchaser(Activity activity)
    {
        super(activity);
    }

    protected Activity getActivity()
    {
        return (Activity) context;
    }

    public boolean isPurchasing()
    {
        return purchasing;
    }

    private void checkNotPurchasing()
    {
        if (purchasing)
        {
            throw new IllegalStateException("IABPurchaser is already purchasing so it cannot be launched again");
        }
    }

    public OnIABPurchaseFinishedListener getPurchaseFinishedListener()
    {
        return purchaseFinishedListener.get();
    }

    public void setPurchaseFinishedListener(OnIABPurchaseFinishedListener purchaseFinishedListener)
    {
        this.purchaseFinishedListener = new WeakReference<>(purchaseFinishedListener);
    }

    public void purchase(SKUDetailsType skuDetails, String extraData, int activityRequestCode)
    {
        checkNotPurchasing();
        if (skuDetails == null)
        {
            throw new NullPointerException("SkuDetails cannot be null");
        }
        if (skuDetails.getProductIdentifier() == null)
        {
            throw new NullPointerException("SkuDetails identifier cannot be null");
        }
        purchasing = true;
        this.skuDetails = skuDetails;
        this.extraData = extraData;
        this.activityRequestCode = activityRequestCode;
        startConnectionSetup();
    }

    @Override protected void handleSetupFinished(IABResponse response)
    {
        super.handleSetupFinished(response);
        startPurchaseActivity();
    }

    @Override protected void handleSetupFailed(IABException exception)
    {
        super.handleSetupFailed(exception);
        handlePurchaseFailedInternal(exception);
    }

    private void handlePurchaseFailedInternal(IABException exception)
    {
        purchasing = false;
        handlePurchaseFailed(exception);
        notifyListenerPurchaseFailed(exception);
    }

    protected void handlePurchaseFailed(IABException exception)
    {
        // Just for children classes
    }

    private void notifyListenerPurchaseFailed(IABException exception)
    {
        OnIABPurchaseFinishedListener listener = getPurchaseFinishedListener();
        if (listener != null)
        {
            listener.onIABPurchaseFailed(this, exception);
        }
    }

    private void handlePurchaseFinishedInternal(SKUPurchase purchase)
    {
        purchasing = false;
        handlePurchaseFinished(purchase);
        notifyListenerPurchaseFinished(purchase);
    }

    protected void handlePurchaseFinished(SKUPurchase purchase)
    {
        // Just for children classes
    }

    private void notifyListenerPurchaseFinished(SKUPurchase purchase)
    {
        OnIABPurchaseFinishedListener listener = getPurchaseFinishedListener();
        if (listener != null)
        {
            listener.onIABPurchaseFinished(this, purchase);
        }
    }

    private void startPurchaseActivity()
    {
        if (!areSubscriptionsSupported() && skuDetails.isOfType(Constants.ITEM_TYPE_SUBS))
        {
            handlePurchaseFailedInternal(new IABSubscriptionUnavailableException("Subscriptions are not available."));
            return;
        }

        try
        {
            Bundle buyIntentBundle = createBuyIntentBundle();
            THLog.d(TAG, "BuyIntentBundle " + buyIntentBundle);

            PendingIntent pendingIntent = buyIntentBundle.getParcelable(Constants.RESPONSE_BUY_INTENT);
            THLog.d(TAG, "Launching buy intent for " + skuDetails + ". Request code: " + activityRequestCode);
            getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(),
                    activityRequestCode, new Intent(),
                    Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
        }
        catch (IntentSender.SendIntentException e)
        {
            THLog.e(TAG, "SendIntentException while launching purchase flow for skuDetails " + skuDetails, e);
            handlePurchaseFailedInternal(new IABSendIntentException("Failed to send intent."));
        }
        catch (RemoteException e)
        {
            THLog.e(TAG, "RemoteException while launching purchase flow for skuDetails " + skuDetails, e);
            handlePurchaseFailedInternal(new IABRemoteException("Remote exception while starting purchase flow"));
        }
        catch (IABException e)
        {
            THLog.e(TAG, "IABException while launching purchase flow for skuDetails " + skuDetails, e);
            handlePurchaseFailedInternal(e);
        }
    }

    private Bundle createBuyIntentBundle() throws RemoteException, IABException
    {
        THLog.d(TAG, "Constructing buy intent for " + skuDetails + ", item type: " + skuDetails.itemType);
        Bundle buyIntentBundle = billingService.getBuyIntent(TARGET_BILLING_API_VERSION3, context.getPackageName(), skuDetails.getProductIdentifier().identifier, skuDetails.type, extraData);
        int response = Constants.getResponseCodeFromBundle(buyIntentBundle);
        if (response != Constants.BILLING_RESPONSE_RESULT_OK)
        {
            THLog.d(TAG, "Unable to buy item, Error response: " + Constants.getStatusCodeDescription(response));
            throw iabExceptionFactory.get().create(response);
        }
        return buyIntentBundle;
    }

    /**
     * Handles an activity result that's part of the purchase flow in in-app billing. If you
     * are calling {@link #startPurchaseActivity()}, then you must call this method from your
     * Activity's {@link android.app.Activity@onActivityResult} method. This method
     * MUST be called from the UI thread of the Activity.
     *
     * @param requestCode The requestCode as you received it.
     * @param resultCode The resultCode as you received it.
     * @param data The data (Intent) as you received it.
     * @return Returns true if the result was related to a purchase flow and was handled;
     *     false if the result was not related to a purchase, in which case you should
     *     handle it normally.
     */
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode != activityRequestCode)
        {
            return false;
        }

        checkNotDisposed();
        checkSetupDone("handleActivityResult");

        if (data == null)
        {
            THLog.w(TAG, "Null data in IAB activity result.");
            handlePurchaseFailedInternal(new IABBadResponseException("Null data in IAB result"));
        }
        else
        {
            int responseCode = Constants.getResponseCodeFromIntent(data);
            String purchaseData = data.getStringExtra(Constants.RESPONSE_INAPP_PURCHASE_DATA);
            String dataSignature = data.getStringExtra(Constants.RESPONSE_INAPP_SIGNATURE);

            if (resultCode == Activity.RESULT_OK && responseCode == Constants.BILLING_RESPONSE_RESULT_OK)
            {
                THLog.d(TAG, "Successful resultcode from purchase activity.");
                THLog.d(TAG, "Purchase data: " + purchaseData);
                THLog.d(TAG, "Data signature: " + dataSignature);
                THLog.d(TAG, "Extras: " + data.getExtras());
                THLog.d(TAG, "Expected item type: " + skuDetails.type);

                if (purchaseData == null || dataSignature == null)
                {
                    THLog.w(TAG, "BUG: either purchaseData or dataSignature is null.");
                    THLog.w(TAG, "Extras: " + data.getExtras().toString());
                    handlePurchaseFailedInternal(new IABUnknownErrorException("IAB returned null purchaseData or dataSignature"));
                }
                else
                {
                    try
                    {
                        SKUPurchase purchase = new SKUPurchase(skuDetails.type, purchaseData, dataSignature);
                        String sku = purchase.getProductIdentifier().identifier;

                        // Verify signature
                        if (!Security.verifyPurchase(Constants.BASE_64_PUBLIC_KEY, purchaseData, dataSignature))
                        {
                            THLog.w(TAG, "Purchase signature verification FAILED for sku " + sku);
                            handlePurchaseFailedInternal(new IABVerificationFailedException("Signature verification failed for sku " + sku));
                        }
                        else
                        {
                            THLog.d(TAG, "Purchase signature successfully verified.");
                            handlePurchaseFinishedInternal(purchase);
                        }
                    }
                    catch (JSONException e)
                    {
                        THLog.e(TAG, "Failed to parse purchase data.", e);
                        handlePurchaseFailedInternal(new IABBadResponseException("Failed to parse purchase data."));
                    }
                }
            }
            else if (resultCode == Activity.RESULT_OK)
            {
                // result code was OK, but in-app billing response was not OK.
                THLog.w(TAG, "Result code was OK but in-app billing response was not OK: " + Constants.getStatusCodeDescription(responseCode));
                handlePurchaseFailedInternal(iabExceptionFactory.get().create(responseCode));
            }
            else if (resultCode == Activity.RESULT_CANCELED)
            {
                THLog.w(TAG, "Purchase canceled - Response: " + Constants.getStatusCodeDescription(responseCode));
                handlePurchaseFailedInternal(iabExceptionFactory.get().create(Constants.IABHELPER_USER_CANCELLED));
            }
            else
            {
                THLog.w(TAG, "Purchase failed. Result code: " + Integer.toString(resultCode)
                        + ". Response: " + Constants.getStatusCodeDescription(responseCode));
                handlePurchaseFailedInternal(iabExceptionFactory.get().create(Constants.IABHELPER_UNKNOWN_PURCHASE_RESPONSE));
            }
        }

        return true;
    }

    /**
     * Callback that notifies when a purchase is finished.
     *  Created with IntelliJ IDEA. User: xavier Date: 11/7/13 Time: 11:00 AM To change this template use File | Settings | File Templates.
     *  */
    public static interface OnIABPurchaseFinishedListener
    {
        /**
         * Called to notify that an in-app purchase finished. If the purchase was successful,
         * then the sku parameter specifies which item was purchased. If the purchase failed,
         * the sku and extraData parameters may or may not be null, depending on how far the purchase
         * process went.
         *
         * @param info The purchase information (null if purchase failed)
         */
        void onIABPurchaseFinished(IABPurchaser purchaser, SKUPurchase info);

        void onIABPurchaseFailed(IABPurchaser purchaser, IABException exception);
    }
}
