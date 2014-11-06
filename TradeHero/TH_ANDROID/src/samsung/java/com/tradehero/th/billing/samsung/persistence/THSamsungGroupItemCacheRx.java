package com.tradehero.th.billing.samsung.persistence;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.persistence.SamsungGroupItemCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache public class THSamsungGroupItemCacheRx
        extends SamsungGroupItemCacheRx<SamsungSKU, SamsungSKUList>
{
    public static int MAX_SIZE = 5;

    //<editor-fold desc="Constructors">
    @Inject public THSamsungGroupItemCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(MAX_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    @Override protected SamsungSKUList createEmptyValue()
    {
        return new SamsungSKUList();
    }
}
