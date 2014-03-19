package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingAvailableTester;
import com.tradehero.common.billing.googleplay.exception.IABException;

/**
 * Created by xavier on 3/19/14.
 */
public class IABBillingAvailableTester
        extends IABServiceConnector
        implements BillingAvailableTester<IABException>
{
    protected int requestCode;
    protected boolean testing;
    protected OnBillingAvailableListener<IABException> billingAvailableListener;

    public IABBillingAvailableTester()
    {
        super();
    }

    @Override public void onDestroy()
    {
        billingAvailableListener = null;
        super.onDestroy();
    }

    @Override public int getRequestCode()
    {
        return requestCode;
    }

    @Override public OnBillingAvailableListener<IABException> getBillingAvailableListener()
    {
        return billingAvailableListener;
    }

    @Override public void setBillingAvailableListener(OnBillingAvailableListener<IABException> billingAvailableListener)
    {
        this.billingAvailableListener = billingAvailableListener;
    }

    private void checkNotTesting()
    {
        if (testing)
        {
            throw new IllegalStateException("IABBillingAvailableTester is already testing so it cannot be launched again");
        }
    }

    @Override public void testBillingAvailable(int requestCode)
    {
        checkNotTesting();
        testing = true;
        this.requestCode = requestCode;
        startConnectionSetup();
    }

    @Override protected void handleSetupFinished(IABResponse response)
    {
        super.handleSetupFinished(response);
        notifyBillingAvailable();
    }

    @Override protected void handleSetupFailed(IABException exception)
    {
        super.handleSetupFailed(exception);
        notifyBillingNotAvailable(exception);
    }

    private void notifyBillingAvailable()
    {
        OnBillingAvailableListener<IABException> billingAvailableListenerCopy = billingAvailableListener;
        if (billingAvailableListenerCopy != null)
        {
            billingAvailableListenerCopy.onBillingAvailable(requestCode);
        }
    }

    private void notifyBillingNotAvailable(IABException exception)
    {
        OnBillingAvailableListener<IABException> billingAvailableListenerCopy = billingAvailableListener;
        if (billingAvailableListenerCopy != null)
        {
            billingAvailableListenerCopy.onBillingNotAvailable(requestCode, exception);
        }
    }
}
