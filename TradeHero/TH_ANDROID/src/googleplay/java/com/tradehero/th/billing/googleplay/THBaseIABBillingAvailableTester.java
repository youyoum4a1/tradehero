package com.tradehero.th.billing.googleplay;

import android.content.Context;
import com.tradehero.common.billing.googleplay.BaseIABBillingAvailableTester;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import dagger.Lazy;
import javax.inject.Inject;
import android.support.annotation.NonNull;

public class THBaseIABBillingAvailableTester
    extends BaseIABBillingAvailableTester
    implements THIABBillingAvailableTester
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseIABBillingAvailableTester(
            @NonNull Context context,
            @NonNull Lazy<IABExceptionFactory> iabExceptionFactory)
    {
        super(context, iabExceptionFactory);
    }
    //</editor-fold>
}
