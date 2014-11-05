package com.tradehero.th.persistence.billing.googleplay;

import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THIABProductDetailTuner;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class THIABProductDetailCache extends ProductDetailCache<IABSKU, THIABProductDetail, THIABProductDetailTuner>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public THIABProductDetailCache(
            @NonNull THIABProductDetailTuner thiabProductDetailTuner,
            @NonNull DTOCacheUtilNew dtoCacheUtilNew)
    {
        super(DEFAULT_MAX_SIZE, thiabProductDetailTuner, dtoCacheUtilNew);
    }
    //</editor-fold>

    @Override @NonNull public THIABProductDetail fetch(@NonNull IABSKU key)
    {
        throw new IllegalStateException("You should not fetch THIABProductDetail individually");
    }
}
