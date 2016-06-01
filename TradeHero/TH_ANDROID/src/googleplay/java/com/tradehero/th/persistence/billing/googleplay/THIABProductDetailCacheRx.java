package com.ayondo.academy.persistence.billing.googleplay;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductDetailCacheRx;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.billing.googleplay.THIABProductDetail;
import com.ayondo.academy.billing.googleplay.THIABProductDetailTuner;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class THIABProductDetailCacheRx extends ProductDetailCacheRx<IABSKU, THIABProductDetail>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public THIABProductDetailCacheRx(
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    @Override public void onNext(@NonNull IABSKU key, @NonNull THIABProductDetail value)
    {
        THIABProductDetailTuner.fineTune(value);
        super.onNext(key, value);
    }
}
