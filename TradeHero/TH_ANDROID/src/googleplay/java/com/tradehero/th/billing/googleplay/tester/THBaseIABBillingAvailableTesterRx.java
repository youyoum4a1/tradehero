package com.ayondo.academy.billing.googleplay.tester;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.tester.BaseIABBillingAvailableTesterRx;
import javax.inject.Inject;

public class THBaseIABBillingAvailableTesterRx
    extends BaseIABBillingAvailableTesterRx
    implements THIABBillingAvailableTesterRx
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseIABBillingAvailableTesterRx(
            int requestCode,
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        super(requestCode, context, iabExceptionFactory);
    }
    //</editor-fold>
}
