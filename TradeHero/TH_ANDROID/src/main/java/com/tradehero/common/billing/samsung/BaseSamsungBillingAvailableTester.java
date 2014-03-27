package com.tradehero.common.billing.samsung;

import android.content.Context;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.tradehero.common.billing.BillingAvailableTester;
import com.tradehero.common.billing.samsung.exception.SamsungException;

/**
 * Created by xavier on 3/27/14.
 */
abstract public class BaseSamsungBillingAvailableTester<SamsungExceptionType extends SamsungException>
    extends BaseSamsungActor
    implements SamsungBillingAvailableTester<SamsungExceptionType>
{
    private OnBillingAvailableListener<SamsungExceptionType> availableListener;

    public BaseSamsungBillingAvailableTester(Context context, int mode)
    {
        super(context, mode);
    }

    @Override public OnBillingAvailableListener<SamsungExceptionType> getBillingAvailableListener()
    {
        return availableListener;
    }

    @Override public void setBillingAvailableListener(OnBillingAvailableListener<SamsungExceptionType> billingAvailableListener)
    {
        this.availableListener = billingAvailableListener;
    }

    // TODO confirm this is the right way to test
    @Override public void testBillingAvailable(int requestCode)
    {
        setRequestCode(requestCode);
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
