package com.androidth.general.common.billing.googleplay.tester;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.BaseIABServiceCaller;
import com.androidth.general.common.billing.googleplay.IABServiceResult;
import com.androidth.general.common.billing.googleplay.exception.IABExceptionFactory;
import com.androidth.general.common.billing.tester.BillingTestResult;
import rx.Observable;
import rx.functions.Func1;

public class BaseIABBillingAvailableTesterRx
        extends BaseIABServiceCaller
        implements IABBillingAvailableTesterRx
{
    //<editor-fold desc="Constructors">
    public BaseIABBillingAvailableTesterRx(
            int requestCode,
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        super(requestCode, context, iabExceptionFactory);
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
