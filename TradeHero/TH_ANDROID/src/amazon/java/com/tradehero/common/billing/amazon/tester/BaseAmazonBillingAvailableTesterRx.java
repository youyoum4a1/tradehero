package com.tradehero.common.billing.amazon.tester;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonActor;
import com.tradehero.common.billing.tester.BaseBillingAvailableTesterRx;
import com.tradehero.common.billing.tester.BillingTestResult;
import rx.Observable;

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

    @NonNull @Override public Observable<BillingTestResult> get()
    {
        try
        {
            Class.forName("com.amazon.device.iap.PurchasingService");
            return Observable.just(new BillingTestResult(getRequestCode()));
        } catch (ClassNotFoundException e)
        {
            return Observable.error(e);
        }
    }
}
