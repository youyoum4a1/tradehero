package com.tradehero.th.persistence.billing.samsung;

import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.samsung.BaseSamsungProductDetail;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.UserCache;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;

@Singleton @UserCache public class SamsungSKUListCache extends ProductIdentifierListCache<SamsungSKU, SamsungSKUListKey, SamsungSKUList>
{
    public static final int MAX_SIZE = 15;

    //<editor-fold desc="Constructors">
    @Inject public SamsungSKUListCache(@NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(MAX_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    @Override public SamsungSKUListKey getKeyForAll()
    {
        return SamsungSKUListKey.getAllKey();
    }

    public void add(BaseSamsungProductDetail<SamsungSKU> detail)
    {
        SamsungSKU sku = detail.getProductIdentifier();
        add(new SamsungSKUListKey(detail.getType()), sku);
        add(getKeyForAll(), sku);
    }

    public void add(SamsungSKUListKey key, SamsungSKU sku)
    {
        SamsungSKUList currentList = get(key);
        if (currentList == null)
        {
            currentList = new SamsungSKUList();
            put(key, currentList);
        }
        if (!currentList.contains(sku))
        {
            currentList.add(sku);
        }
    }
}
