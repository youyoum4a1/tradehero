package com.tradehero.th.billing.samsung;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.BaseSamsungProductIdentifierFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import javax.inject.Inject;

public class THBaseSamsungProductIdentifierFetcherHolder
    extends BaseSamsungProductIdentifierFetcherHolder<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        THSamsungProductIdentifierFetcher,
        SamsungException>
    implements THSamsungProductIdentifierFetcherHolder
{
    @NonNull protected final Context context;
    protected final int mode;
    @NonNull protected final THSamsungExceptionFactory samsungExceptionFactory;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungProductIdentifierFetcherHolder(
            @NonNull Context context,
            @ForSamsungBillingMode int mode,
            @NonNull THSamsungExceptionFactory samsungExceptionFactory)
    {
        super();
        this.context = context;
        this.mode = mode;
        this.samsungExceptionFactory = samsungExceptionFactory;
    }
    //</editor-fold>

    @NonNull @Override protected THSamsungProductIdentifierFetcher createProductIdentifierFetcher(int requestCode)
    {
        return new THBaseSamsungProductIdentifierFetcher(requestCode, context, mode, samsungExceptionFactory);
    }
}
