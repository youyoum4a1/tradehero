package com.tradehero.common.billing.amazon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;

abstract public class BaseAmazonBillingAvailableTester<AmazonExceptionType extends AmazonException>
    extends BaseAmazonActor
    implements AmazonBillingAvailableTester<AmazonExceptionType>
{
    @Nullable private OnBillingAvailableListener<AmazonExceptionType> availableListener;

    //<editor-fold desc="Constructors">
    public BaseAmazonBillingAvailableTester(
            int request,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super(request, purchasingService);
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        setBillingAvailableListener(null);
        super.onDestroy();
    }

    @Override @Nullable public OnBillingAvailableListener<AmazonExceptionType> getBillingAvailableListener()
    {
        return availableListener;
    }

    @Override public void setBillingAvailableListener(@Nullable OnBillingAvailableListener<AmazonExceptionType> billingAvailableListener)
    {
        this.availableListener = billingAvailableListener;
    }

    // TODO confirm this is the right way to test
    @Override public void testBillingAvailable()
    {
        notifyBillingAvailable();
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

    @SuppressWarnings("UnusedDeclaration")
    protected void notifyBillingNotAvailable(AmazonExceptionType exception)
    {
        OnBillingAvailableListener<AmazonExceptionType> listenerCopy = availableListener;
        if (listenerCopy != null)
        {
            listenerCopy.onBillingNotAvailable(getRequestCode(), exception);
        }
    }
}
