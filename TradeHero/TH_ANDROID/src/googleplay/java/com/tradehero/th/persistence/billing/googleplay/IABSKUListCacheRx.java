package com.androidth.general.persistence.billing.googleplay;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.ProductIdentifierListCacheRx;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.IABSKUList;
import com.androidth.general.common.billing.googleplay.IABSKUListKey;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache public class IABSKUListCacheRx extends ProductIdentifierListCacheRx<IABSKU, IABSKUListKey, IABSKUList>
{
    public static final int MAX_SIZE = 5;

    //<editor-fold desc="Constructors">
    @Inject public IABSKUListCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(MAX_SIZE, dtoCacheUtil);
    }
    //</editor-fold>
}
