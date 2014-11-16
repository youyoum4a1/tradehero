package com.tradehero.common.billing.googleplay.tester;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABServiceCaller;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.tester.BillingTestResult;
import rx.Observable;

public class BaseIABBillingAvailableTesterRx
        extends BaseIABServiceCaller<BillingTestResult>
        implements IABBillingAvailableTesterRx
{
    protected boolean testing;

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
                .map(result -> new BillingTestResult(getRequestCode()));
    }
}
