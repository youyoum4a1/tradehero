package com.tradehero.th.billing.samsung.persistence;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.persistence.SamsungGroupItemCache;
import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.UserCache;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton @UserCache public class THSamsungGroupItemCache
        extends SamsungGroupItemCache<SamsungSKU, SamsungSKUList>
{
    public static int MAX_SIZE = 5;

    //<editor-fold desc="Constructors">
    @Inject public THSamsungGroupItemCache(@NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(MAX_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    @Override protected SamsungSKUList createEmptyValue()
    {
        return new SamsungSKUList();
    }
}
