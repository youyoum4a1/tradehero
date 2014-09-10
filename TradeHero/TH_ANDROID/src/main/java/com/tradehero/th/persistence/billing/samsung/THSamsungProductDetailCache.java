package com.tradehero.th.persistence.billing.samsung;

import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.billing.samsung.THSamsungProductDetail;
import com.tradehero.th.billing.samsung.THSamsungProductDetailTuner;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class THSamsungProductDetailCache extends ProductDetailCache<SamsungSKU, THSamsungProductDetail, THSamsungProductDetailTuner>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    @NotNull protected final SamsungSKUListCache samsungSKUListCache;

    //<editor-fold desc="Constructors">
    @Inject public THSamsungProductDetailCache(
            @NotNull THSamsungProductDetailTuner thSamsungProductDetailTuner,
            @NotNull SamsungSKUListCache samsungSKUListCache)
    {
        super(DEFAULT_MAX_SIZE, thSamsungProductDetailTuner);
        this.samsungSKUListCache = samsungSKUListCache;
    }
    //</editor-fold>

    @Override @NotNull public THSamsungProductDetail fetch(@NotNull SamsungSKU key)
    {
        throw new IllegalStateException("You should not fetch THSamsungProductDetail individually");
    }

    @Override public THSamsungProductDetail put(@NotNull SamsungSKU key, @NotNull THSamsungProductDetail value)
    {
        samsungSKUListCache.add(value);
        return super.put(key, value);
    }
}
