package com.tradehero.common.billing.googleplay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.vending.billing.IInAppBillingService;
import com.tradehero.common.utils.THLog;

import java.util.Collections;
import java.util.Map;

/** Created by julien on 5/11/13 */
abstract public class IABServiceConnector
{
    public static final String TAG = IABServiceConnector.class.getSimpleName();
    public final static String INTENT_VENDING_PACKAGE = "com.android.vending";
    public final static String INTENT_VENDING_SERVICE_BIND = "com.android.vending.billing.InAppBillingService.BIND";

    protected Context context;

    protected IInAppBillingService service;
    protected ServiceConnection serviceConnection;

    private boolean subscriptionSupported;
    private boolean setupDone = false;
    boolean disposed = false;

    protected ConnectorListener listener;

    public IABServiceConnector(Context ctx)
    {
        this.context = ctx;
        if (this.context == null)
        {
            throw new NullPointerException("Context cannot be null");
        }
    }

    public void startConnectionSetup()
    {
        checkNotDisposed();
        checkNotSetup();

        THLog.d(TAG, "Starting in-app billing setup.");

        setupServiceConnection();
        bindBillingServiceIfAvailable();
    }

    protected void bindBillingServiceIfAvailable()
    {
        Intent serviceIntent = getBillingBindIntent();

        if (isServiceAvailable(serviceIntent))
        {
            // service available to handle that Intent
            context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        else
        {
            // no service available to handle that Intent
            IABException exception = new IABException(Constants.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE, "Billing service unavailable on device.");
            handleSetupFailed(exception);
            notifyListenerSetupFailed(exception);
        }
    }

    public Intent getBillingBindIntent()
    {
        Intent serviceIntent = new Intent(INTENT_VENDING_SERVICE_BIND);
        serviceIntent.setPackage(INTENT_VENDING_PACKAGE);
        return serviceIntent;
    }

    protected boolean isServiceAvailable(Intent serviceIntent)
    {
        return !context.getPackageManager().queryIntentServices(serviceIntent, 0).isEmpty();
    }

    /**
     * Dispose of object, releasing resources. It's very important to call this method when you are done with this object. It will release any
     * resources used by it such as service connections. Naturally, once the object is disposed of, it can't be used again.
     */
    public void dispose()
    {
        THLog.d(TAG, "Disposing.");
        setupDone = false;
        if (serviceConnection != null)
        {
            THLog.d(TAG, "Unbinding from service.");
            if (context != null)
            {
                context.unbindService(serviceConnection);
            }
        }
        disposed = true;
        context = null;
        serviceConnection = null;
        service = null;
        listener = null;
    }

    private ServiceConnection setupServiceConnection()
    {
        if (serviceConnection != null)
        {
            return serviceConnection;
        }

        serviceConnection = new ServiceConnection()
        {
            @Override public void onServiceDisconnected(ComponentName name)
            {
                THLog.d(TAG, "Billing service disconnected.");
                service = null;
            }

            @Override public void onServiceConnected(ComponentName name, IBinder binderService)
            {
                THLog.d(TAG, "Billing service connected.");
                service = IInAppBillingService.Stub.asInterface(binderService);
                String packageName = context.getPackageName();
                try
                {
                    THLog.d(TAG, "Checking for in-app billing 3 support.");

                    // check for in-app billing v3 support
                    int responseStatus = service.isBillingSupported(3, packageName, Constants.ITEM_TYPE_INAPP);
                    if (responseStatus != Constants.BILLING_RESPONSE_RESULT_OK)
                    {
                        if (listener != null)
                        {
                            listener.onSetupFinished(IABServiceConnector.this,
                                    new IABResponse(responseStatus, "Error checking for billing v3 support."));
                        }

                        // if in-app purchases aren't supported, neither are subscriptions.
                        subscriptionSupported = false;
                        return;
                    }
                    THLog.d(TAG, "In-app billing version 3 supported for " + packageName);

                    // check for v3 subscriptions support
                    responseStatus = service.isBillingSupported(3, packageName, Constants.ITEM_TYPE_SUBS);
                    if (responseStatus == Constants.BILLING_RESPONSE_RESULT_OK)
                    {
                        THLog.d(TAG, "Subscriptions AVAILABLE.");
                        subscriptionSupported = true;
                    }
                    else
                    {
                        THLog.d(TAG, "Subscriptions NOT AVAILABLE. Response: " + responseStatus);
                    }

                    setupDone = true;
                }
                catch (RemoteException e)
                {
                    if (listener != null)
                    {
                        listener.onSetupFinished(IABServiceConnector.this, new IABResponse(Constants.IABHELPER_REMOTE_EXCEPTION,
                                "RemoteException while setting up in-app billing."));
                    }
                    e.printStackTrace();
                    return;
                }

                if (listener != null)
                {
                    listener.onSetupFinished(IABServiceConnector.this, new IABResponse(Constants.BILLING_RESPONSE_RESULT_OK, "Setup successful."));
                }
            }
        };
        return serviceConnection;
    }

    private void checkNotDisposed()
    {
        if (disposed)
        {
            throw new IllegalStateException("IabHelper was disposed of, so it cannot be used.");
        }
    }

    private void checkNotSetup()
    {
        if (setupDone)
        {
            throw new IllegalStateException("IAB helper is already set up.");
        }
    }

    //<editor-fold desc="Accessors">
    public boolean areSubscriptionsSupported()
    {
        return this.subscriptionSupported;
    }

    public boolean isSetupDone()
    {
        return setupDone;
    }

    public ConnectorListener getListener()
    {
        return listener;
    }

    public void setListener(ConnectorListener listener)
    {
        this.listener = listener;
    }
    //</editor-fold>

    abstract protected void handleSetupFinished(IABResponse response);

    abstract protected void handleSetupFailed(IABException exception);

    protected void notifyListenerSetupFinished(IABResponse response)
    {
        ConnectorListener listenerCopy = listener;
        if (listenerCopy != null)
        {
            listenerCopy.onSetupFinished(this, response);
        }
    }

    protected void notifyListenerSetupFailed(IABException exception)
    {
        ConnectorListener listenerCopy = listener;
        if (listenerCopy != null)
        {
            listenerCopy.onSetupFailed(this, exception);
        }
    }

    public static interface ConnectorListener
    {
        void onSetupFinished(IABServiceConnector connector, IABResponse response);
        void onSetupFailed(IABServiceConnector connector, IABException exception);
    }
}
