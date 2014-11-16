package com.tradehero.common.billing.amazon.tester;

import com.tradehero.common.billing.amazon.AmazonActor;
import com.tradehero.common.billing.tester.BaseBillingAvailableTesterRx;
import com.tradehero.common.billing.tester.BillingTestResult;

abstract public class BaseAmazonBillingAvailableTesterRx
        extends BaseBillingAvailableTesterRx
        implements AmazonBillingAvailableTesterRx, AmazonActor
{
    //<editor-fold desc="Constructors">
    public BaseAmazonBillingAvailableTesterRx(int request)
    {
        super(request);
    }
    //</editor-fold>

    protected void testBillingAvailable()
    {
        try
        {
            Class.forName("com.amazon.device.iap.PurchasingService");
            subject.onNext(new BillingTestResult(getRequestCode()));
            subject.onCompleted();
        } catch (ClassNotFoundException e)
        {
            subject.onError(e);
        }
    }
}
