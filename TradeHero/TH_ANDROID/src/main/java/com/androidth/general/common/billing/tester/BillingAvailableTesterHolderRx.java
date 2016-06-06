package com.androidth.general.common.billing.tester;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.RequestCodeHolder;
import rx.Observable;

public interface BillingAvailableTesterHolderRx
    extends RequestCodeHolder
{
    @NonNull Observable<BillingTestResult> get(int requestCode);
}
