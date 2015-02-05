package com.tradehero.common.billing.tester;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.RequestCodeHolder;
import rx.Observable;

public interface BillingAvailableTesterHolderRx
    extends RequestCodeHolder
{
    @NonNull Observable<BillingTestResult> get(int requestCode);
}
