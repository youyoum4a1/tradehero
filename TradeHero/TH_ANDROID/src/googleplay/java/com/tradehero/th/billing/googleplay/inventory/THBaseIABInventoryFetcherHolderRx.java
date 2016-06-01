package com.ayondo.academy.billing.googleplay.inventory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.inventory.BaseIABInventoryFetcherHolderRx;
import com.tradehero.common.billing.googleplay.inventory.IABInventoryFetcherRx;
import com.ayondo.academy.billing.googleplay.THIABProductDetail;
import java.util.List;
import javax.inject.Inject;

public class THBaseIABInventoryFetcherHolderRx
        extends BaseIABInventoryFetcherHolderRx<
        IABSKU,
        THIABProductDetail>
        implements THIABInventoryFetcherHolderRx
{
    @NonNull protected final Context context;
    @NonNull protected final IABExceptionFactory iabExceptionFactory;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABInventoryFetcherHolderRx(
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        this.context = context;
        this.iabExceptionFactory = iabExceptionFactory;
    }
    //</editor-fold>

    @NonNull @Override protected IABInventoryFetcherRx<IABSKU, THIABProductDetail> createFetcher(
            int requestCode,
            @NonNull List<IABSKU> productIdentifiers)
    {
        return new THBaseIABInventoryFetcherRx(requestCode, productIdentifiers, context, iabExceptionFactory);
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
