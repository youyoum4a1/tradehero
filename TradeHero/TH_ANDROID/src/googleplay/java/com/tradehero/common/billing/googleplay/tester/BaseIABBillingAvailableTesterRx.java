package com.tradehero.common.billing.googleplay.tester;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABServiceCaller;
import com.tradehero.common.billing.googleplay.BillingServiceBinderObservable;
import com.tradehero.common.billing.googleplay.IABServiceResult;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.tester.BillingTestResult;
import rx.Observable;
import rx.functions.Func1;

public class BaseIABBillingAvailableTesterRx
        extends BaseIABServiceCaller
        implements IABBillingAvailableTesterRx
{
    protected boolean testing;

    //<editor-fold desc="Constructors">
    public BaseIABBillingAvailableTesterRx(
            int requestCode,
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory,
            @NonNull BillingServiceBinderObservable billingServiceBinderObservable)
    {
        super(requestCode, context, iabExceptionFactory, billingServiceBinderObservable);
    }
    //</editor-fold>

    @NonNull @Override public Observable<BillingTestResult> get()
    {
        return getBillingServiceResult()
                .map(new Func1<IABServiceResult, BillingTestResult>()
                {
                    @Override public BillingTestResult call(IABServiceResult result)
                    {
                        return new BillingTestResult(BaseIABBillingAvailableTesterRx.this.getRequestCode());
                    }
                });
    }
}
