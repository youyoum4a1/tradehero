package com.androidth.general.billing.samsung.persistence;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.persistence.SamsungPurchaseCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.billing.samsung.THSamsungOrderId;
import com.androidth.general.billing.samsung.THSamsungPurchase;
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
