package com.tradehero.th.persistence.billing.googleplay;

import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.UserCache;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton @UserCache public class IABSKUListCache extends ProductIdentifierListCache<IABSKU, IABSKUListKey, IABSKUList>
{
    public static final int MAX_SIZE = 5;

    //<editor-fold desc="Constructors">
    @Inject public IABSKUListCache(@NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(MAX_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    @Override public IABSKUListKey getKeyForAll()
    {
        return IABSKUListKey.getAll();
    }
}
