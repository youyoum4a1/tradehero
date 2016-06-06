package com.androidth.general.persistence.billing.googleplay;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.ProductDetailCacheRx;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.billing.googleplay.THIABProductDetail;
import com.androidth.general.billing.googleplay.THIABProductDetailTuner;
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
