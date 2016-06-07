package com.androidth.general.billing.googleplay.identifier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.IABSKUList;
import com.androidth.general.common.billing.googleplay.IABSKUListKey;
import com.androidth.general.common.billing.googleplay.exception.IABExceptionFactory;
import com.androidth.general.common.billing.googleplay.identifier.BaseIABProductIdentifierFetcherHolderRx;
import com.androidth.general.common.billing.googleplay.identifier.IABProductIdentifierFetcherRx;
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

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABProductIdentifierFetcherHolderRx(
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        super();
        this.context = context;
        this.iabExceptionFactory = iabExceptionFactory;
    }
    //</editor-fold>

    @NonNull @Override protected IABProductIdentifierFetcherRx<IABSKUListKey, IABSKU, IABSKUList> createFetcher(int requestCode)
    {
        return new THBaseIABProductIdentifierFetcherRx(requestCode, context, iabExceptionFactory);
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
