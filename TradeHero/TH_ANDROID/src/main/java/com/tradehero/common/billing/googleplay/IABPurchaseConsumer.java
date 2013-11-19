package com.tradehero.common.billing.googleplay;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.RemoteException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.billing.googleplay.exceptions.IABInvalidConsumptionException;
import com.tradehero.common.billing.googleplay.exceptions.IABMissingTokenException;
import com.tradehero.common.billing.googleplay.exceptions.IABRemoteException;
import com.tradehero.common.utils.THLog;
import java.lang.ref.WeakReference;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 3:23 PM To change this template use File | Settings | File Templates. */
public class IABPurchaseConsumer<
            IABSKUType extends IABSKU,
            IABOrderIdType extends IABOrderId,
            IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends IABServiceConnector
{
    public static final String TAG = IABPurchaseConsumer.class.getSimpleName();

    private boolean consuming = false;
    protected IABPurchaseType purchase;
    private WeakReference<OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException>> consumptionFinishedListener = new WeakReference<>(null);

    public IABPurchaseConsumer(Activity activity)
    {
        super(activity);
    }

    protected Activity getActivity()
    {
        return (Activity) context;
    }

    public boolean isConsuming()
    {
        return consuming;
    }

    private void checkNotConsuming()
    {
        if (consuming)
        {
            throw new IllegalStateException("IABPurchaseConsumer is already consuming so it cannot be launched again");
        }
    }

    public OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> getConsumptionFinishedListener()
    {
        return consumptionFinishedListener.get();
    }

    /**
     * the listener should be strongly referenced elsewhere
     * @param consumptionFinishedListener
     */
    public void setConsumptionFinishedListener(OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> consumptionFinishedListener)
    {
        this.consumptionFinishedListener = new WeakReference<>(consumptionFinishedListener);
    }

    public void consume(IABPurchaseType purchase)
    {
        checkNotConsuming();

        if (purchase == null)
        {
            throw new IllegalArgumentException("Purchase cannot be null");
        }
        if (purchase.getProductIdentifier() == null)
        {
            throw new IllegalArgumentException("Product Identifier cannot be null");
        }
        if (purchase.getProductIdentifier().identifier == null)
        {
            throw new IllegalArgumentException("Product Identifier's identifier cannot be null");
        }

        if (purchase.getType().equals(Constants.ITEM_TYPE_INAPP))
        {
            handleConsumeFailedInternal(new IABInvalidConsumptionException("Can only consume inApp purchase types"));
        }
        else if (purchase.getToken() == null)
        {
            handleConsumeFailedInternal(new IABMissingTokenException("Token cannot be null"));
        }
        else
        {
            this.purchase = purchase;
            consuming = true;
            startConnectionSetup();
        }
    }

    @Override protected void handleSetupFinished(IABResponse response)
    {
        super.handleSetupFinished(response);
        consumeEffectivelyAsync();
    }

    @Override protected void handleSetupFailed(IABException exception)
    {
        super.handleSetupFailed(exception);
        handleConsumeFailedInternal(exception);
    }

    private void handleConsumeFailedInternal(IABException exception)
    {
        consuming = false;
        handleConsumeFailed(exception);
        notifyListenerConsumeFailed(exception);
    }

    private void handleConsumeFailed(IABException exception)
    {
        // Just for children classes
    }

    private void notifyListenerConsumeFailed(IABException exception)
    {
        OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> listener = getConsumptionFinishedListener();
        if (listener != null)
        {
            listener.onIABConsumptionFailed(this, exception);
        }
    }

    private void handleConsumeFinishedInternal(IABPurchaseType purchase)
    {
        consuming = false;
        handleConsumeFinished(purchase);
        notifyListenerConsumeFinished(purchase);
    }

    private void handleConsumeFinished(IABPurchaseType purchase)
    {
        // Just for children classes
    }

    private void notifyListenerConsumeFinished(IABPurchaseType purchase)
    {
        OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> listener = getConsumptionFinishedListener();
        if (listener != null)
        {
            listener.onIABConsumptionFinished(this, purchase);
        }
    }

    private void consumeEffectivelyAsync()
    {
        AsyncTask<Void, Void, Void> consumeTask = new AsyncTask<Void, Void, Void>()
        {
            private IABException exception;

            @Override protected Void doInBackground(Void... params)
            {
                try
                {
                    consumeEffectively();
                }
                catch (RemoteException e)
                {
                    THLog.e(TAG, "Remote Exception while fetching inventory.", e);
                    exception = new IABRemoteException("RemoteException while fetching IAB", e);
                }
                catch (IABException e)
                {
                    THLog.e(TAG, "IAB error.", e);
                    exception = e;
                }

                return null;
            }

            @Override protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
                if (exception != null)
                {
                    handleConsumeFailedInternal(exception);
                }
                else
                {
                    handleConsumeFinishedInternal(purchase);
                }
            }
        };
        consumeTask.execute();
    }

    private void consumeEffectively() throws RemoteException, IABException
    {
        String sku = this.purchase.getProductIdentifier().identifier;
        String token = this.purchase.getToken();
        THLog.d(TAG, "Consuming sku: " + sku + ", token: " + token);
        int response = this.billingService.consumePurchase(3, context.getPackageName(), token);
        if (response != Constants.BILLING_RESPONSE_RESULT_OK)
        {
            throw iabExceptionFactory.get().create(response);
        }
    }

    public static interface OnIABConsumptionFinishedListener<
            IABSKUType extends IABSKU,
            IABOrderIdType extends IABOrderId,
            IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
            IABExceptionType extends IABException>
    {
        void onIABConsumptionFinished(IABPurchaseConsumer purchaseConsumer, IABPurchaseType info);

        void onIABConsumptionFailed(IABPurchaseConsumer purchaseConsumer, IABExceptionType exception);
    }
}
