package com.tradehero.th.persistence.billing.samsung;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductDetailCacheRx;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.billing.samsung.THSamsungProductDetail;
import com.tradehero.th.billing.samsung.THSamsungProductDetailTuner;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache public class THSamsungProductDetailCacheRx extends ProductDetailCacheRx<SamsungSKU, THSamsungProductDetail>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    @NonNull protected final SamsungSKUListCacheRx samsungSKUListCache;

    //<editor-fold desc="Constructors">
    @Inject public THSamsungProductDetailCacheRx(
            @NonNull SamsungSKUListCacheRx samsungSKUListCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.samsungSKUListCache = samsungSKUListCache;
    }
    //</editor-fold>

    @Override public void onNext(@NonNull SamsungSKU key, @NonNull THSamsungProductDetail value)
    {
        THSamsungProductDetailTuner.fineTune(value);
        samsungSKUListCache.onNext(value);
        super.onNext(key, value);
    }
}
