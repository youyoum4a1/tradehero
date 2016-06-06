package com.androidth.general.persistence.billing.googleplay;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.common.persistence.billing.googleplay.IABPurchaseCacheRx;
import com.androidth.general.billing.googleplay.THIABOrderId;
import com.androidth.general.billing.googleplay.THIABPurchase;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache public class THIABPurchaseCacheRx
        extends IABPurchaseCacheRx<
                IABSKU,
                THIABOrderId,
                THIABPurchase>
{
    public static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public THIABPurchaseCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
    }
    //</editor-fold>
}
