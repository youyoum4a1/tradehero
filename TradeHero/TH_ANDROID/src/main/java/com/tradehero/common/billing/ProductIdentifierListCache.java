package com.tradehero.common.billing;

import com.tradehero.common.persistence.StraightDTOCache;
import java.util.Map;
import timber.log.Timber;

abstract public class ProductIdentifierListCache<
            ProductIdentifierType extends ProductIdentifier,
            ProductIdentifierListKeyType extends ProductIdentifierListKey,
            ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>>
        extends StraightDTOCache<ProductIdentifierListKeyType, ProductIdentifierListType>
{
    public ProductIdentifierListCache(int maxSize)
    {
        super(maxSize);
    }

    @Override protected ProductIdentifierListType fetch(ProductIdentifierListKeyType key) throws Throwable
    {
        throw new IllegalArgumentException("Do not fetch on this cache");
    }

    abstract public ProductIdentifierListKeyType getKeyForAll();

    public void put(Map<ProductIdentifierListKeyType, ProductIdentifierListType> typedLists)
    {
        Timber.d("Put map size %d", typedLists.size());
        for (Map.Entry<ProductIdentifierListKeyType, ProductIdentifierListType> entry : typedLists.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override public ProductIdentifierListType put(ProductIdentifierListKeyType key,
            ProductIdentifierListType value)
    {
        Timber.d("Put %s", key);
        return super.put(key, value);
    }
}
