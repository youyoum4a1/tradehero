package com.ayondo.academy.billing.samsung.identifier;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.SamsungBillingMode;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.identifier.BaseSamsungProductIdentifierFetcherHolderRx;
import com.ayondo.academy.billing.samsung.exception.THSamsungExceptionFactory;
import javax.inject.Inject;

public class THBaseSamsungProductIdentifierFetcherHolderRx
        extends BaseSamsungProductIdentifierFetcherHolderRx<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList>
        implements THSamsungProductIdentifierFetcherHolderRx
{
    @NonNull protected final Context context;
    @SamsungBillingMode protected final int mode;
    @NonNull protected final THSamsungExceptionFactory samsungExceptionFactory;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungProductIdentifierFetcherHolderRx(
            @NonNull Context context,
            @SamsungBillingMode int mode,
            @NonNull THSamsungExceptionFactory samsungExceptionFactory)
    {
        super();
        this.context = context;
        this.mode = mode;
        this.samsungExceptionFactory = samsungExceptionFactory;
    }
    //</editor-fold>

    @NonNull @Override protected THSamsungProductIdentifierFetcherRx createFetcher(int requestCode)
    {
        return new THBaseSamsungProductIdentifierFetcherRx(requestCode, context, mode);
    }
}
