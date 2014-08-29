package com.tradehero.common.billing.amazon;

import android.content.Context;
import com.amazon.device.iap.PurchasingService;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class BaseAmazonBillingAvailableTester<AmazonExceptionType extends AmazonException>
    extends BaseAmazonActor
    implements AmazonBillingAvailableTester<AmazonExceptionType>
{
    @Nullable private OnBillingAvailableListener<AmazonExceptionType> availableListener;

    //<editor-fold desc="Description">
    public BaseAmazonBillingAvailableTester(@NotNull Context appContext)
    {
        super(appContext);
    }
    //</editor-fold>

    @Override @Nullable public OnBillingAvailableListener<AmazonExceptionType> getBillingAvailableListener()
    {
        return availableListener;
    }

    @Override public void setBillingAvailableListener(@Nullable OnBillingAvailableListener<AmazonExceptionType> billingAvailableListener)
    {
        this.availableListener = billingAvailableListener;
    }

    // TODO confirm this is the right way to test
    @Override public void testBillingAvailable(int requestCode)
    {
        setRequestCode(requestCode);
        try
        {
            PurchasingService.registerListener(appContext, middlePurchasingListener);
            notifyBillingAvailable();
        }
        catch (Exception e)
        {
            notifyBillingNotAvailable(createNotAvailable(e));
        }
    }

    abstract protected AmazonExceptionType createNotAvailable(Exception cause);

    protected void notifyBillingAvailable()
    {
        OnBillingAvailableListener<AmazonExceptionType> listenerCopy = availableListener;
        if (listenerCopy != null)
        {
            listenerCopy.onBillingAvailable(getRequestCode());
        }
    }

    protected void notifyBillingNotAvailable(AmazonExceptionType exception)
    {
        OnBillingAvailableListener<AmazonExceptionType> listenerCopy = availableListener;
        if (listenerCopy != null)
        {
            listenerCopy.onBillingNotAvailable(getRequestCode(), exception);
        }
    }
}
