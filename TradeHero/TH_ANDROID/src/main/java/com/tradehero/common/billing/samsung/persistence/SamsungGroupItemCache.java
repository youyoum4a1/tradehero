package com.tradehero.common.billing.samsung.persistence;

import com.tradehero.common.billing.samsung.BaseSamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungItemGroup;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

abstract public class SamsungGroupItemCache<
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>>
    extends StraightDTOCacheNew<SamsungItemGroup, SamsungSKUListType>
{
    //<editor-fold desc="Constructors">
    public SamsungGroupItemCache(int maxSize)
    {
        super(maxSize);
    }
    //</editor-fold>

    @Override @NotNull public SamsungSKUListType fetch(@NotNull SamsungItemGroup key) throws Throwable
    {
        throw new IllegalArgumentException("Cannot fetch on this cache");
    }

    public void add(SamsungSKUType id)
    {
        Timber.d("Add %s", id);
        SamsungSKUListType currentValue = get(id.getGroupId());
        if (currentValue == null)
        {
            Timber.d("Creating Empty");
            currentValue = createEmptyValue();
            put(id.getGroupId(), currentValue);
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

    public void add(Collection<? extends SamsungSKUType> ids)
    {
        if (ids != null)
        {
            for (SamsungSKUType id : ids)
            {
                add(id);
            }
        }
    }

    abstract protected SamsungSKUListType createEmptyValue();
}
