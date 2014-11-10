package com.tradehero.th.persistence.billing;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductDetailCacheRx;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.billing.amazon.THAmazonProductDetail;
import com.tradehero.th.billing.amazon.THAmazonProductDetailTuner;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class THAmazonProductDetailCacheRx extends ProductDetailCacheRx<AmazonSKU, THAmazonProductDetail, THAmazonProductDetailTuner>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public THAmazonProductDetailCacheRx(
            @NonNull THAmazonProductDetailTuner thAmazonProductDetailTuner,
            @NonNull DTOCacheUtilRx dtoCacheUtilNew)
    {
        super(DEFAULT_MAX_SIZE, thAmazonProductDetailTuner, dtoCacheUtilNew);
    }
    //</editor-fold>
}
