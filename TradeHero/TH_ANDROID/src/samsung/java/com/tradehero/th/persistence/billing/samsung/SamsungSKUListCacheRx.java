package com.ayondo.academy.persistence.billing.samsung;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductIdentifierListCacheRx;
import com.tradehero.common.billing.samsung.BaseSamsungProductDetail;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache public class SamsungSKUListCacheRx extends ProductIdentifierListCacheRx<SamsungSKU, SamsungSKUListKey, SamsungSKUList>
{
    public static final int MAX_SIZE = 15;

    //<editor-fold desc="Constructors">
    @Inject public SamsungSKUListCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(MAX_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    @NonNull public SamsungSKUListKey getKeyForAll()
    {
        return SamsungSKUListKey.getAllKey();
    }

    public void onNext(BaseSamsungProductDetail<SamsungSKU> detail)
    {
        SamsungSKU sku = detail.getProductIdentifier();
        onNext(new SamsungSKUListKey(detail.getType()), sku);
        onNext(getKeyForAll(), sku);
    }

    public void onNext(SamsungSKUListKey key, SamsungSKU sku)
    {
        SamsungSKUList currentList = getCachedValue(key);
        if (currentList == null)
        {
            currentList = new SamsungSKUList();
            onNext(key, currentList);
        }
        if (!currentList.contains(sku))
        {
            currentList.add(sku);
        }
    }
}
