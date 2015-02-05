package com.tradehero.th.billing.googleplay.tester;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BillingServiceBinderObservable;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.tester.BaseIABBillingAvailableTesterHolderRx;
import javax.inject.Inject;

public class THBaseIABBillingAvailableTesterHolderRx
    extends BaseIABBillingAvailableTesterHolderRx
    implements THIABBillingAvailableTesterHolderRx
{
    @NonNull protected final Context context;
    @NonNull protected final IABExceptionFactory iabExceptionFactory;
    @NonNull protected final BillingServiceBinderObservable billingServiceBinderObservable;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABBillingAvailableTesterHolderRx(
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory,
            @NonNull BillingServiceBinderObservable billingServiceBinderObservable)
    {
        this.context = context;
        this.iabExceptionFactory = iabExceptionFactory;
        this.billingServiceBinderObservable = billingServiceBinderObservable;
    }
    //</editor-fold>

    @NonNull @Override protected THBaseIABBillingAvailableTesterRx createTester(int requestCode)
    {
        return new THBaseIABBillingAvailableTesterRx(requestCode, context, iabExceptionFactory, billingServiceBinderObservable);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
