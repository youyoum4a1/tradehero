package com.androidth.general.common.billing.tester;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.RequestCodeActor;
import rx.Observable;

public interface BillingAvailableTesterRx
    extends RequestCodeActor
{
    @NonNull Observable<BillingTestResult> get();
}
