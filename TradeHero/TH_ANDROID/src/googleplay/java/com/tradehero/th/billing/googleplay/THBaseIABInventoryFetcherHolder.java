package com.tradehero.th.billing.googleplay;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABInventoryFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import dagger.Lazy;
import javax.inject.Inject;

public class THBaseIABInventoryFetcherHolder
        extends BaseIABInventoryFetcherHolder<
        IABSKU,
        THIABProductDetail,
        THIABInventoryFetcher>
        implements THIABInventoryFetcherHolder
{
    @NonNull protected final Context context;
    @NonNull protected final Lazy<IABExceptionFactory> iabExceptionFactory;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABInventoryFetcherHolder(
            @NonNull Context context,
            @NonNull Lazy<IABExceptionFactory> iabExceptionFactory)
    {
        super();
        this.context = context;
        this.iabExceptionFactory = iabExceptionFactory;
    }
    //</editor-fold>

    @Override protected THIABInventoryFetcher createInventoryFetcher(int requestCode)
    {
        return new THBaseIABInventoryFetcher(requestCode, context, iabExceptionFactory);
    }
}
