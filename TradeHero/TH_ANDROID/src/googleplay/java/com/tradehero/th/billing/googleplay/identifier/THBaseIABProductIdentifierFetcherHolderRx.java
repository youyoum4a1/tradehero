package com.tradehero.th.billing.googleplay.identifier;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BillingServiceBinderObservable;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.identifier.BaseIABProductIdentifierFetcherHolderRx;
import com.tradehero.common.billing.googleplay.identifier.IABProductIdentifierFetcherRx;
import javax.inject.Inject;

public class THBaseIABProductIdentifierFetcherHolderRx
    extends BaseIABProductIdentifierFetcherHolderRx<
            IABSKUListKey,
            IABSKU,
            IABSKUList>
    implements THIABProductIdentifierFetcherHolderRx
{
    @NonNull private final Context context;
    @NonNull private final IABExceptionFactory iabExceptionFactory;
    @NonNull private final BillingServiceBinderObservable billingServiceBinderObservable;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABProductIdentifierFetcherHolderRx(
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory,
            @NonNull BillingServiceBinderObservable billingServiceBinderObservable)
    {
        super();
        this.context = context;
        this.iabExceptionFactory = iabExceptionFactory;
        this.billingServiceBinderObservable = billingServiceBinderObservable;
    }
    //</editor-fold>

    @NonNull @Override protected IABProductIdentifierFetcherRx<IABSKUListKey, IABSKU, IABSKUList> createFetcher(int requestCode)
    {
        return new THBaseIABProductIdentifierFetcherRx(requestCode, context, iabExceptionFactory, billingServiceBinderObservable);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
