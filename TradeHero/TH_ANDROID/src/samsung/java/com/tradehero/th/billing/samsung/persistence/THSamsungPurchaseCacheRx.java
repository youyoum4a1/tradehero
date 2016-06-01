package com.ayondo.academy.billing.samsung.persistence;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.persistence.SamsungPurchaseCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.billing.samsung.THSamsungOrderId;
import com.ayondo.academy.billing.samsung.THSamsungPurchase;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache public class THSamsungPurchaseCacheRx
    extends SamsungPurchaseCacheRx<
            SamsungSKU,
            THSamsungOrderId,
            THSamsungPurchase>
{
    public static final int MAX_SIZE = 300;

    //<editor-fold desc="Constructors">
    @Inject public THSamsungPurchaseCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(MAX_SIZE, dtoCacheUtil);
    }
    //</editor-fold>
}
