package com.ayondo.academy.persistence.billing;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonPurchaseCacheRx;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.billing.amazon.THAmazonOrderId;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache public class THAmazonPurchaseCacheRx
        extends AmazonPurchaseCacheRx<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase>
{
    public static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public THAmazonPurchaseCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
    }
    //</editor-fold>
}
