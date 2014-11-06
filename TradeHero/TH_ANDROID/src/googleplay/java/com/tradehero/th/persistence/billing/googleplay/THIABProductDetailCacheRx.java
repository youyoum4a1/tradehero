package com.tradehero.th.persistence.billing.googleplay;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductDetailCacheRx;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THIABProductDetailTuner;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class THIABProductDetailCacheRx extends ProductDetailCacheRx<IABSKU, THIABProductDetail, THIABProductDetailTuner>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public THIABProductDetailCacheRx(
            @NonNull THIABProductDetailTuner thiabProductDetailTuner,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, thiabProductDetailTuner, dtoCacheUtil);
    }
    //</editor-fold>
}
