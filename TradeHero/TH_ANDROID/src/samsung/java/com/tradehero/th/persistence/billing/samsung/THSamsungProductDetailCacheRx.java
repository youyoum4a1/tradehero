package com.androidth.general.persistence.billing.samsung;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.ProductDetailCacheRx;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.billing.samsung.THSamsungProductDetail;
import com.androidth.general.billing.samsung.THSamsungProductDetailTuner;
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
