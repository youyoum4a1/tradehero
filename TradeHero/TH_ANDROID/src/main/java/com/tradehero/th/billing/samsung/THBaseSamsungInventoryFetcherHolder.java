package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.BaseSamsungInventoryFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class THBaseSamsungInventoryFetcherHolder
    extends BaseSamsungInventoryFetcherHolder<
        SamsungSKU,
        THSamsungProductDetail,
        THSamsungInventoryFetcher,
        SamsungException>
    implements THSamsungInventoryFetcherHolder
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungInventoryFetcherHolder(
            @NotNull Provider<THSamsungInventoryFetcher> thSamsungInventoryFetcherProvider)
    {
        super(thSamsungInventoryFetcherProvider);
    }
    //</editor-fold>
}
