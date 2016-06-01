package com.ayondo.academy.persistence.billing;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductIdentifierListCacheRx;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache public class AmazonSKUListCacheRx extends ProductIdentifierListCacheRx<AmazonSKU, AmazonSKUListKey, AmazonSKUList>
{
    public static final int MAX_SIZE = 10;

    //<editor-fold desc="Constructors">
    @Inject public AmazonSKUListCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(MAX_SIZE, dtoCacheUtil);
    }
    //</editor-fold>
}
