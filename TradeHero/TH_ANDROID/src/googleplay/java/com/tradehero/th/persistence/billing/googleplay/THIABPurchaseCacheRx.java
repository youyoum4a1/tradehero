package com.ayondo.academy.persistence.billing.googleplay;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.common.persistence.billing.googleplay.IABPurchaseCacheRx;
import com.ayondo.academy.billing.googleplay.THIABOrderId;
import com.ayondo.academy.billing.googleplay.THIABPurchase;
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
