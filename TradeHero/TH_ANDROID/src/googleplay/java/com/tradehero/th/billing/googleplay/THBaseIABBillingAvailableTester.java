package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABBillingAvailableTester;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.th.activities.CurrentActivityHolder;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.Lazy;

public class THBaseIABBillingAvailableTester
    extends BaseIABBillingAvailableTester
    implements THIABBillingAvailableTester
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseIABBillingAvailableTester(
            @NotNull CurrentActivityHolder currentActivityHolder,
            @NotNull Lazy<IABExceptionFactory> iabExceptionFactory)
    {
        super(currentActivityHolder, iabExceptionFactory);
    }
    //</editor-fold>
}
