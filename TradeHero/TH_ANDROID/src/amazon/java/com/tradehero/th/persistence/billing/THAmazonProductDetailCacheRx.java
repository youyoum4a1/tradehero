package com.ayondo.academy.persistence.billing;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductDetailCacheRx;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.billing.amazon.THAmazonProductDetail;
import com.ayondo.academy.billing.amazon.THAmazonProductDetailTuner;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class THAmazonProductDetailCacheRx extends ProductDetailCacheRx<AmazonSKU, THAmazonProductDetail>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public THAmazonProductDetailCacheRx(
            @NonNull DTOCacheUtilRx dtoCacheUtilNew)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtilNew);
    }
    //</editor-fold>

    @Override public void onNext(@NonNull AmazonSKU key, @NonNull THAmazonProductDetail value)
    {
        THAmazonProductDetailTuner.fineTune(value);
        super.onNext(key, value);
    }
}
