package com.androidth.general.billing.samsung.inventory;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.samsung.SamsungBillingMode;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.inventory.BaseSamsungInventoryFetcherHolderRx;
import com.androidth.general.common.billing.samsung.inventory.SamsungInventoryFetcherRx;
import com.androidth.general.billing.samsung.THSamsungProductDetail;
import com.androidth.general.billing.samsung.exception.THSamsungExceptionFactory;
import java.util.List;
import javax.inject.Inject;

public class THBaseSamsungInventoryFetcherHolderRx
        extends BaseSamsungInventoryFetcherHolderRx<
        SamsungSKU,
        THSamsungProductDetail>
        implements THSamsungInventoryFetcherHolderRx
{
    @NonNull protected final Context context;
    @SamsungBillingMode protected final int mode;
    @NonNull protected final THSamsungExceptionFactory samsungExceptionFactory;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungInventoryFetcherHolderRx(
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

    @NonNull @Override protected SamsungInventoryFetcherRx<SamsungSKU, THSamsungProductDetail> createFetcher(int requestCode,
            @NonNull List<SamsungSKU> productIdentifiers)
    {
        return new THBaseSamsungInventoryFetcherRx(requestCode, context, mode, productIdentifiers);
    }
}
