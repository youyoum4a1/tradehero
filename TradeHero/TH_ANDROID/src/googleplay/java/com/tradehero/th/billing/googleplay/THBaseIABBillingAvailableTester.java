package com.tradehero.th.billing.googleplay;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABBillingAvailableTester;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import dagger.Lazy;

public class THBaseIABBillingAvailableTester
    extends BaseIABBillingAvailableTester
    implements THIABBillingAvailableTester
{
    //<editor-fold desc="Constructors">
    public THBaseIABBillingAvailableTester(
            int requestCode,
            @NonNull Context context,
            @NonNull Lazy<IABExceptionFactory> iabExceptionFactory)
    {
        super(requestCode, context, iabExceptionFactory);
    }
    //</editor-fold>
}
