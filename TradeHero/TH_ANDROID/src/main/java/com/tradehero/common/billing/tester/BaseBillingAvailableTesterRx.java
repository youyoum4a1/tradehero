package com.tradehero.common.billing.tester;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseRequestCodeReplayActor;
import rx.Observable;

abstract public class BaseBillingAvailableTesterRx
        extends BaseRequestCodeReplayActor<BillingTestResult>
        implements BillingAvailableTesterRx
{
    //<editor-fold desc="Constructors">
    protected BaseBillingAvailableTesterRx(int requestCode)
    {
        super(requestCode);
    }
    //</editor-fold>

    @Override @NonNull public Observable<BillingTestResult> get()
    {
        return replayObservable;
    }
}
