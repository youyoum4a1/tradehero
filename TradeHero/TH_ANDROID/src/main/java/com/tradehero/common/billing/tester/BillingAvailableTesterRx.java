package com.tradehero.common.billing.tester;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.RequestCodeActor;
import rx.Observable;

public interface BillingAvailableTesterRx
    extends RequestCodeActor
{
    @NonNull Observable<BillingTestResult> get();
}
