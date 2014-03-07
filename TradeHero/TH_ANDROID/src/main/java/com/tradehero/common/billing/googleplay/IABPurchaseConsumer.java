package com.tradehero.common.billing.googleplay;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.RemoteException;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABInvalidConsumptionException;
import com.tradehero.common.billing.googleplay.exception.IABMissingTokenException;
import com.tradehero.common.billing.googleplay.exception.IABRemoteException;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 3:23 PM To change this template use File | Settings | File Templates. */
abstract public class IABPurchaseConsumer<
            IABSKUType extends IABSKU,
            IABOrderIdType extends IABOrderId,
            IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends IABServiceConnector
{
    private int requestCode;
    private boolean consuming = false;
    protected IABPurchaseType purchase;
    private OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> consumptionFinishedListener;

    //<editor-fold desc="Constructors">
    public IABPurchaseConsumer()
    {
        super();
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        consumptionFinishedListener = null;
        super.onDestroy();
    }

    protected Activity getActivity()
    {
        return currentActivityHolder.getCurrentActivity();
    }

    abstract protected IABPurchaseCache<IABSKUType, IABOrderIdType, IABPurchaseType> getPurchaseCache();

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
        return consumptionFinishedListener;
    }

    public void setConsumptionFinishedListener(OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> consumptionFinishedListener)
    {
        this.consumptionFinishedListener = consumptionFinishedListener;
    }

    public void consume(int requestCode, IABPurchaseType purchase)
    {
        checkNotConsuming();
        this.requestCode = requestCode;

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

        if (purchase.getType().equals(IABConstants.ITEM_TYPE_SUBS))
        {
            handleConsumeSkippedInternal(purchase);
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
            listener.onPurchaseConsumeFailed(requestCode, purchase, exception);
        }
    }

    private void handleConsumeFinishedInternal(IABPurchaseType purchase)
    {
        consuming = false;
        getPurchaseCache().invalidate(purchase.getProductIdentifier());
        handleConsumeFinished(purchase);
        notifyListenerConsumeFinished(purchase);
    }

    private void handleConsumeFinished(IABPurchaseType purchase)
    {
        // Just for children classes
    }

    private void handleConsumeSkippedInternal(IABPurchaseType purchase)
    {
        consuming = false;
        handleConsumeSkipped(purchase);
        notifyListenerConsumeFinished(purchase);
    }

    private void handleConsumeSkipped(IABPurchaseType purchase)
    {
        // Just for children classes
    }

    private void notifyListenerConsumeFinished(IABPurchaseType purchase)
    {
        OnIABConsumptionFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> listener = getConsumptionFinishedListener();
        if (listener != null)
        {
            listener.onPurchaseConsumed(requestCode, purchase);
        }
    }

    private void consumeEffectivelyAsync()
    {
        AsyncTask<Void, Void, Void> consumeTask = new AsyncTask<Void, Void, Void>()
        {
            private IABException exception;

            @Override protected Void doInBackground(Void... params)
            {
                if (!disposed)
                {
                    try
                    {
                        consumeEffectively();
                    }
                    catch (RemoteException e)
                    {
                        Timber.e("Remote Exception while fetching inventory.", e);
                        exception = new IABRemoteException("RemoteException while fetching IAB", e);
                    }
                    catch (IABException e)
                    {
                        Timber.e("IAB error.", e);
                        exception = e;
                    }
                }

                return null;
            }

            @Override protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
                if (disposed)
                {
                    // Do nothing
                }
                else if (exception != null)
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
        Timber.d("Consuming sku: %s, token: %s", sku, token);
        int response = this.billingService.consumePurchase(3, currentActivityHolder.getCurrentActivity().getPackageName(), token);
        if (response != IABConstants.BILLING_RESPONSE_RESULT_OK)
        {
            throw iabExceptionFactory.get().create(response);
        }
        Timber.d("Consumed successfully sku: %s", sku);
    }

    public static interface OnIABConsumptionFinishedListener<
            IABSKUType extends IABSKU,
            IABOrderIdType extends IABOrderId,
            IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
            IABExceptionType extends IABException>
    {
        void onPurchaseConsumed(int requestCode, IABPurchaseType purchase);
        void onPurchaseConsumeFailed(int requestCode, IABPurchaseType purchase, IABExceptionType exception);
    }
}
