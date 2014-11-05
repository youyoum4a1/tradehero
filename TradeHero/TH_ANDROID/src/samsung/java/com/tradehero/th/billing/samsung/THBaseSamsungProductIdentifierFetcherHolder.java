package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.BaseSamsungProductIdentifierFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import javax.inject.Provider;
import android.support.annotation.NonNull;

public class THBaseSamsungProductIdentifierFetcherHolder
    extends BaseSamsungProductIdentifierFetcherHolder<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        THSamsungProductIdentifierFetcher,
        SamsungException>
    implements THSamsungProductIdentifierFetcherHolder
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungProductIdentifierFetcherHolder(
            @NonNull Provider<THSamsungProductIdentifierFetcher> thSamsungProductIdentifierFetcherProvider)
    {
        super(thSamsungProductIdentifierFetcherProvider);
    }
    //</editor-fold>
}
