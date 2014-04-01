package com.tradehero.common.billing.samsung.persistence;

import com.tradehero.common.billing.samsung.BaseSamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungItemGroup;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.persistence.StraightDTOCache;
import java.util.Collection;
import timber.log.Timber;

/**
 * Created by xavier on 4/1/14.
 */
abstract public class SamsungGroupItemCache<
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>>
    extends StraightDTOCache<SamsungItemGroup, SamsungSKUListType>
{
    public SamsungGroupItemCache(int maxSize)
    {
        super(maxSize);
    }

    @Override protected SamsungSKUListType fetch(SamsungItemGroup key) throws Throwable
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
