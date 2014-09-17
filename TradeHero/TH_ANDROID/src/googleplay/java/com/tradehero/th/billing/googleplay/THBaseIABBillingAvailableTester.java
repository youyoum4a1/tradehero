package com.tradehero.th.billing.googleplay;

import android.content.Context;
import com.tradehero.common.billing.googleplay.BaseIABBillingAvailableTester;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class THBaseIABBillingAvailableTester
    extends BaseIABBillingAvailableTester
    implements THIABBillingAvailableTester
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseIABBillingAvailableTester(
            @NotNull Context context,
            @NotNull Lazy<IABExceptionFactory> iabExceptionFactory)
    {
        super(context, iabExceptionFactory);
    }
    //</editor-fold>
}
