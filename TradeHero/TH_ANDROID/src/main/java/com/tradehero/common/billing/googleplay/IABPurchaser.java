package com.tradehero.common.billing.googleplay;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.RemoteException;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.googleplay.exceptions.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.billing.googleplay.exceptions.IABRemoteException;
import com.tradehero.common.billing.googleplay.exceptions.IABSendIntentException;
import com.tradehero.common.billing.googleplay.exceptions.IABSubscriptionUnavailableException;
import com.tradehero.common.billing.googleplay.exceptions.IABUnknownErrorException;
import com.tradehero.common.billing.googleplay.exceptions.IABVerificationFailedException;
import java.lang.ref.WeakReference;
import org.json.JSONException;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/7/13 Time: 11:05 AM To change this template use File | Settings | File Templates. */
abstract public class IABPurchaser<
        IABSKUType extends IABSKU,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends IABServiceConnector
    implements BillingPurchaser<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseOrderType,
        IABPurchaseType,
        IABException>
{
    private boolean purchasing = false;
    private IABPurchaseOrderType purchaseOrder;
    private int activityRequestCode;
    private WeakReference<OnPurchaseFinishedListener<IABSKUType, IABPurchaseOrderType, IABOrderIdType, IABPurchaseType, IABException>> purchaseFinishedListener = new WeakReference<>(null);

    public IABPurchaser(Activity activity)
    {
        super(activity);
    }

    abstract protected IABPurchaseType createPurchase(String itemType, String purchaseData, String dataSignature) throws JSONException;
    abstract protected IABProductDetailType getProductDetails(IABSKUType iabskuType);

    protected Activity getActivity()
    {
        return (Activity) context;
    }

    @Override public int getRequestCode()
    {
        return activityRequestCode;
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

    @Override public OnPurchaseFinishedListener<IABSKUType, IABPurchaseOrderType, IABOrderIdType, IABPurchaseType, IABException> getPurchaseFinishedListener()
    {
        return purchaseFinishedListener.get();
    }

    @Override public void setPurchaseFinishedListener(OnPurchaseFinishedListener<IABSKUType, IABPurchaseOrderType, IABOrderIdType, IABPurchaseType, IABException> purchaseFinishedListener)
    {
        Timber.d("setPurchaseFinishedListener %s", purchaseFinishedListener.getClass().getSimpleName());
        this.purchaseFinishedListener = new WeakReference<>(purchaseFinishedListener);
    }

    @Override public void purchase(int activityRequestCode, IABPurchaseOrderType purchaseOrder)
    {
        checkNotPurchasing();
        if (purchaseOrder == null)
        {
            throw new NullPointerException("purchaseOrder cannot be null");
        }
        if (purchaseOrder.getProductIdentifier() == null)
        {
            throw new NullPointerException("purchaseOrder identifier cannot be null");
        }
        purchasing = true;
        this.purchaseOrder = purchaseOrder;
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
        OnPurchaseFinishedListener listener = getPurchaseFinishedListener();
        if (listener != null)
        {
            listener.onPurchaseFailed(activityRequestCode, purchaseOrder, exception);
        }
    }

    private void handlePurchaseFinishedInternal(IABPurchaseType purchase)
    {
        purchasing = false;
        handlePurchaseFinished(purchase);
        notifyListenerPurchaseFinished(purchase);
    }

    protected void handlePurchaseFinished(IABPurchaseType purchase)
    {
        // Just for children classes
    }

    private void notifyListenerPurchaseFinished(IABPurchaseType purchase)
    {
        OnPurchaseFinishedListener listener = getPurchaseFinishedListener();
        if (listener != null)
        {
            listener.onPurchaseFinished(activityRequestCode, purchaseOrder, purchase);
        }
    }

    private void startPurchaseActivity()
    {

        if (!areSubscriptionsSupported() && getProductDetails(purchaseOrder.getProductIdentifier()).isOfType(Constants.ITEM_TYPE_SUBS))
        {
            handlePurchaseFailedInternal(new IABSubscriptionUnavailableException("Subscriptions are not available."));
            return;
        }

        try
        {
            Bundle buyIntentBundle = createBuyIntentBundle();
            Timber.d("BuyIntentBundle " + buyIntentBundle);

            PendingIntent pendingIntent = buyIntentBundle.getParcelable(Constants.RESPONSE_BUY_INTENT);
            Timber.d("Launching buy intent for %s. Request code: %d", getProductDetails(purchaseOrder.getProductIdentifier()), activityRequestCode);
            getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(),
                    activityRequestCode, new Intent(),
                    Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
        }
        catch (IntentSender.SendIntentException e)
        {
            Timber.e("SendIntentException while launching purchase flow for skuDetails %s", getProductDetails(purchaseOrder.getProductIdentifier()), e);
            handlePurchaseFailedInternal(new IABSendIntentException("Failed to send intent."));
        }
        catch (RemoteException e)
        {
            Timber.e("RemoteException while launching purchase flow for skuDetails %s", getProductDetails(purchaseOrder.getProductIdentifier()), e);
            handlePurchaseFailedInternal(new IABRemoteException("Remote exception while starting purchase flow"));
        }
        catch (IABException e)
        {
            Timber.e("IABException while launching purchase flow for skuDetails %s", getProductDetails(purchaseOrder.getProductIdentifier()), e);
            handlePurchaseFailedInternal(e);
        }
    }

    private Bundle createBuyIntentBundle() throws RemoteException, IABException
    {
        Timber.d("Constructing buy intent for %s, item type: %s",
                getProductDetails(purchaseOrder.getProductIdentifier()),
                getProductDetails(purchaseOrder.getProductIdentifier()).getType());
        Bundle buyIntentBundle = billingService.getBuyIntent(
                TARGET_BILLING_API_VERSION3,
                context.getPackageName(),
                purchaseOrder.getProductIdentifier().identifier,
                getProductDetails(purchaseOrder.getProductIdentifier()).getType(),
                purchaseOrder.getDeveloperPayload());
        int response = Constants.getResponseCodeFromBundle(buyIntentBundle);
        if (response != Constants.BILLING_RESPONSE_RESULT_OK)
        {
            Timber.d("Unable to buy item, Error response: %s", Constants.getStatusCodeDescription(response));
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
        Timber.d("handleActivityResult requestCode: %d, resultCode: %d", requestCode, resultCode);
        if (requestCode != activityRequestCode)
        {
            Timber.w("handleActivityResult. This requestcode was not for me");
            return false;
        }

        checkNotDisposed();
        checkSetupDone("handleActivityResult");

        if (data == null)
        {
            Timber.w("Null data in IAB activity result.");
            handlePurchaseFailedInternal(new IABBadResponseException("Null data in IAB result"));
        }
        else
        {
            int responseCode = Constants.getResponseCodeFromIntent(data);
            String purchaseData = data.getStringExtra(Constants.RESPONSE_INAPP_PURCHASE_DATA);
            String dataSignature = data.getStringExtra(Constants.RESPONSE_INAPP_SIGNATURE);

            if (resultCode == Activity.RESULT_OK && responseCode == Constants.BILLING_RESPONSE_RESULT_OK)
            {
                Timber.d("Successful resultCode from purchase activity.");
                Timber.d("Purchase data: %s" + purchaseData);
                Timber.d("Data signature: %s" + dataSignature);
                Timber.d("Extras: %s" + data.getExtras());
                Timber.d("Expected item type: %s" + getProductDetails(purchaseOrder.getProductIdentifier()).getType());

                if (purchaseData == null || dataSignature == null)
                {
                    Timber.w("BUG: either purchaseData or dataSignature is null.");
                    Timber.w("Extras: %s" + data.getExtras().toString());
                    handlePurchaseFailedInternal(new IABUnknownErrorException("IAB returned null purchaseData or dataSignature"));
                }
                else
                {
                    try
                    {
                        IABPurchaseType purchase = createPurchase(getProductDetails(purchaseOrder.getProductIdentifier()).getType(), purchaseData, dataSignature);
                        String sku = purchase.getProductIdentifier().identifier;

                        // Verify signature
                        if (!Security.verifyPurchase(Constants.BASE_64_PUBLIC_KEY, purchaseData, dataSignature))
                        {
                            Timber.w("Purchase signature verification FAILED for sku " + sku);
                            handlePurchaseFailedInternal(new IABVerificationFailedException("Signature verification failed for sku " + sku));
                        }
                        else
                        {
                            Timber.d("Purchase signature successfully verified.");
                            handlePurchaseFinishedInternal(purchase);
                        }
                    }
                    catch (JSONException e)
                    {
                        Timber.e("Failed to parse purchase data.", e);
                        handlePurchaseFailedInternal(new IABBadResponseException("Failed to parse purchase data."));
                    }
                }
            }
            else if (resultCode == Activity.RESULT_OK)
            {
                // result code was OK, but in-app billing response was not OK.
                Timber.w("Result code was OK but in-app billing response was not OK: %s", Constants.getStatusCodeDescription(responseCode));
                handlePurchaseFailedInternal(iabExceptionFactory.get().create(responseCode));
            }
            else if (resultCode == Activity.RESULT_CANCELED)
            {
                Timber.w("Purchase canceled - Response: " + Constants.getStatusCodeDescription(responseCode));
                handlePurchaseFailedInternal(iabExceptionFactory.get().create(Constants.IABHELPER_USER_CANCELLED));
            }
            else
            {
                Timber.w("Purchase failed. Result code: %s. Response: %s", Integer.toString(resultCode), Constants.getStatusCodeDescription(responseCode));
                handlePurchaseFailedInternal(iabExceptionFactory.get().create(Constants.IABHELPER_UNKNOWN_PURCHASE_RESPONSE));
            }
        }

        return true;
    }
}
