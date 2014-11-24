package com.tradehero.th.billing.googleplay;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABInventoryFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import javax.inject.Inject;
import javax.inject.Provider;

public class THBaseIABInventoryFetcherHolder
    extends BaseIABInventoryFetcherHolder<
        IABSKU,
        THIABProductDetail,
        THIABInventoryFetcher>
    implements THIABInventoryFetcherHolder
{
    @NonNull protected final Provider<THIABInventoryFetcher> thiabInventoryFetcherProvider;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABInventoryFetcherHolder(@NonNull Provider<THIABInventoryFetcher> thiabInventoryFetcherProvider)
    {
        super();
        this.thiabInventoryFetcherProvider = thiabInventoryFetcherProvider;
    }
    //</editor-fold>

    @Override protected THIABInventoryFetcher createInventoryFetcher()
    {
        return thiabInventoryFetcherProvider.get();
    }
}
