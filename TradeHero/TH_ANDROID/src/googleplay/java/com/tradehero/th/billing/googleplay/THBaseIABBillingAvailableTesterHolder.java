package com.tradehero.th.billing.googleplay;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABBillingAvailableTesterHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import dagger.Lazy;
import javax.inject.Inject;

public class THBaseIABBillingAvailableTesterHolder
        extends BaseIABBillingAvailableTesterHolder<THIABBillingAvailableTester, IABException>
        implements THIABBillingAvailableTesterHolder
{
    @NonNull protected final Context context;
    @NonNull protected final Lazy<IABExceptionFactory> iabExceptionFactory;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABBillingAvailableTesterHolder(
            @NonNull Context context,
            @NonNull Lazy<IABExceptionFactory> iabExceptionFactory)
    {
        super();
        this.context = context;
        this.iabExceptionFactory = iabExceptionFactory;
    }
    //</editor-fold>

    @NonNull @Override protected THIABBillingAvailableTester createBillingAvailableTester(int requestCode)
    {
        return new THBaseIABBillingAvailableTester(requestCode, context, iabExceptionFactory);
    }
}
