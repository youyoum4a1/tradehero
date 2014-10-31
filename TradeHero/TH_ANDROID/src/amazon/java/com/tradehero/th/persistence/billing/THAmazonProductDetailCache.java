package com.tradehero.th.persistence.billing;

import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.th.billing.amazon.THAmazonProductDetail;
import com.tradehero.th.billing.amazon.THAmazonProductDetailTuner;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class THAmazonProductDetailCache extends ProductDetailCache<AmazonSKU, THAmazonProductDetail, THAmazonProductDetailTuner>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public THAmazonProductDetailCache(
            @NotNull THAmazonProductDetailTuner thAmazonProductDetailTuner,
            @NotNull DTOCacheUtilNew dtoCacheUtilNew)
    {
        super(DEFAULT_MAX_SIZE, thAmazonProductDetailTuner, dtoCacheUtilNew);
    }
    //</editor-fold>

    @Override @NotNull public THAmazonProductDetail fetch(@NotNull AmazonSKU key)
    {
        throw new IllegalStateException("You should not fetch THAmazonProductDetail individually");
    }
}
