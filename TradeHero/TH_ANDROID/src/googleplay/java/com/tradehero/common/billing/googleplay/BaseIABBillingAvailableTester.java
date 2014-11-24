package com.tradehero.common.billing.googleplay;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import dagger.Lazy;
import javax.inject.Inject;

public class BaseIABBillingAvailableTester
        extends IABServiceConnector
        implements IABBillingAvailableTester<IABException>
{
    protected int requestCode;
    protected boolean testing;
    @Nullable protected OnBillingAvailableListener<IABException> billingAvailableListener;

    //<editor-fold desc="Constructors">
    @Inject public BaseIABBillingAvailableTester(
            @NonNull Context context,
            @NonNull Lazy<IABExceptionFactory> iabExceptionFactory)
    {
        super(context, iabExceptionFactory);
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        billingAvailableListener = null;
        super.onDestroy();
    }

    @Override public int getRequestCode()
    {
        return requestCode;
    }

    @Override @Nullable public OnBillingAvailableListener<IABException> getBillingAvailableListener()
    {
        return billingAvailableListener;
    }

    @Override public void setBillingAvailableListener(@Nullable OnBillingAvailableListener<IABException> billingAvailableListener)
    {
        this.billingAvailableListener = billingAvailableListener;
    }

    private void checkNotTesting()
    {
        if (testing)
        {
            throw new IllegalStateException("BaseIABBillingAvailableTester is already testing so it cannot be launched again");
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
