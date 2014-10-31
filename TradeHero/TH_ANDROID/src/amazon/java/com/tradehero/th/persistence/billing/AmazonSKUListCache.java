package com.tradehero.th.persistence.billing;

import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.persistence.DTOCacheUtilNew;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class AmazonSKUListCache extends ProductIdentifierListCache<AmazonSKU, AmazonSKUListKey, AmazonSKUList>
{
    public static final int MAX_SIZE = 5;

    //<editor-fold desc="Constructors">
    @Inject public AmazonSKUListCache(@NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(MAX_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    @Override public AmazonSKUListKey getKeyForAll()
    {
        // TODO any better?
        return null;
    }
}
