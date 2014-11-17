package com.tradehero.th.billing.samsung;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.BaseSamsungInventoryFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import javax.inject.Inject;

public class THBaseSamsungInventoryFetcherHolder
    extends BaseSamsungInventoryFetcherHolder<
        SamsungSKU,
        THSamsungProductDetail,
        THSamsungInventoryFetcher,
        SamsungException>
    implements THSamsungInventoryFetcherHolder
{
    @NonNull protected final Context context;
    protected final int mode;
    @NonNull protected final THSamsungExceptionFactory samsungExceptionFactory;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungInventoryFetcherHolder(
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

    @NonNull @Override protected THSamsungInventoryFetcher createInventoryFetcher(int requestCode)
    {
        return new THBaseSamsungInventoryFetcher(requestCode, context, mode, samsungExceptionFactory);
    }
}
