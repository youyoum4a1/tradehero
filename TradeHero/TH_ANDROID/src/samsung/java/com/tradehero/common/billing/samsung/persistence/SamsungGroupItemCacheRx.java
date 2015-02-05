package com.tradehero.common.billing.samsung.persistence;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.BaseSamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungItemGroup;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import java.util.Collection;
import timber.log.Timber;

abstract public class SamsungGroupItemCacheRx<
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>>
    extends BaseDTOCacheRx<SamsungItemGroup, SamsungSKUListType>
{
    //<editor-fold desc="Constructors">
    public SamsungGroupItemCacheRx(int maxSize,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(maxSize, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(SamsungSKUType id)
    {
        Timber.d("Add %s", id);
        SamsungSKUListType currentValue = getCachedValue(id.getGroupId());
        if (currentValue == null)
        {
            Timber.d("Creating Empty");
            currentValue = createEmptyValue();
            onNext(id.getGroupId(), currentValue);
        }
        if (!currentValue.contains(id))
        {
            Timber.d("Adding value");
            currentValue.add(id);
        }
        else
        {
            Timber.d("Value already in");
        }
    }

    public void onNext(Collection<? extends SamsungSKUType> ids)
    {
        if (ids != null)
        {
            for (SamsungSKUType id : ids)
            {
                onNext(id);
            }
        }
    }

    abstract protected SamsungSKUListType createEmptyValue();
}
