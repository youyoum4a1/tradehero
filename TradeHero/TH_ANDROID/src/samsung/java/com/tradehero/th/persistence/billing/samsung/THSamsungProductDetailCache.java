package com.tradehero.th.persistence.billing.samsung;

import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.billing.samsung.THSamsungProductDetail;
import com.tradehero.th.billing.samsung.THSamsungProductDetailTuner;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;

@Singleton @UserCache public class THSamsungProductDetailCache extends ProductDetailCache<SamsungSKU, THSamsungProductDetail, THSamsungProductDetailTuner>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    @NonNull protected final SamsungSKUListCache samsungSKUListCache;

    //<editor-fold desc="Constructors">
    @Inject public THSamsungProductDetailCache(
            @NonNull THSamsungProductDetailTuner thSamsungProductDetailTuner,
            @NonNull SamsungSKUListCache samsungSKUListCache,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, thSamsungProductDetailTuner, dtoCacheUtil);
        this.samsungSKUListCache = samsungSKUListCache;
    }
    //</editor-fold>

    @Override @NonNull public THSamsungProductDetail fetch(@NonNull SamsungSKU key)
    {
        throw new IllegalStateException("You should not fetch THSamsungProductDetail individually");
    }

    @Override public THSamsungProductDetail put(@NonNull SamsungSKU key, @NonNull THSamsungProductDetail value)
    {
        samsungSKUListCache.add(value);
        return super.put(key, value);
    }
}
