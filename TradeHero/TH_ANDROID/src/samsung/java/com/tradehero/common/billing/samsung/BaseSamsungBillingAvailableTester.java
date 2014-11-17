package com.tradehero.common.billing.samsung;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.tradehero.common.billing.samsung.exception.SamsungException;

abstract public class BaseSamsungBillingAvailableTester<SamsungExceptionType extends SamsungException>
    extends BaseSamsungActor
    implements SamsungBillingAvailableTester<SamsungExceptionType>
{
    @Nullable private OnBillingAvailableListener<SamsungExceptionType> availableListener;

    //<editor-fold desc="Description">
    public BaseSamsungBillingAvailableTester(int requestCode, @NonNull Context context, int mode)
    {
        super(requestCode, context, mode);
    }
    //</editor-fold>

    @Override @Nullable public OnBillingAvailableListener<SamsungExceptionType> getBillingAvailableListener()
    {
        return availableListener;
    }

    @Override public void setBillingAvailableListener(@Nullable OnBillingAvailableListener<SamsungExceptionType> billingAvailableListener)
    {
        this.availableListener = billingAvailableListener;
    }

    // TODO confirm this is the right way to test
    @Override public void testBillingAvailable()
    {
        mIapHelper.bindIapService(this);
    }

    @Override public void onBindIapFinished(int result)
    {
        if (result == SamsungIapHelper.IAP_RESPONSE_RESULT_OK)
        {
            notifyBillingAvailable();
        }
        else
        {
            notifyBillingNotAvailable(createSamsungException(result));
        }
    }

    abstract protected SamsungExceptionType createSamsungException(int result);

    protected void notifyBillingAvailable()
    {
        OnBillingAvailableListener<SamsungExceptionType> listenerCopy = availableListener;
        if (listenerCopy != null)
        {
            listenerCopy.onBillingAvailable(getRequestCode());
        }
    }

    protected void notifyBillingNotAvailable(SamsungExceptionType exception)
    {
        OnBillingAvailableListener<SamsungExceptionType> listenerCopy = availableListener;
        if (listenerCopy != null)
        {
            listenerCopy.onBillingNotAvailable(getRequestCode(), exception);
        }
    }
}
