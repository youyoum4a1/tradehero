package com.ayondo.academy.billing.googleplay.tester;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.tester.BaseIABBillingAvailableTesterHolderRx;
import javax.inject.Inject;

public class THBaseIABBillingAvailableTesterHolderRx
    extends BaseIABBillingAvailableTesterHolderRx
    implements THIABBillingAvailableTesterHolderRx
{
    @NonNull protected final Context context;
    @NonNull protected final IABExceptionFactory iabExceptionFactory;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABBillingAvailableTesterHolderRx(
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        this.context = context;
        this.iabExceptionFactory = iabExceptionFactory;
    }
    //</editor-fold>

    @NonNull @Override protected THBaseIABBillingAvailableTesterRx createTester(int requestCode)
    {
        return new THBaseIABBillingAvailableTesterRx(requestCode, context, iabExceptionFactory);
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
