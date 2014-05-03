package com.tradehero.common.billing;

import com.tradehero.common.persistence.StraightDTOCache;
import java.util.Map;


abstract public class ProductIdentifierListCache<
            ProductIdentifierType extends ProductIdentifier,
            ProductIdentifierListKeyType extends ProductIdentifierListKey,
            ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>>
        extends StraightDTOCache<ProductIdentifierListKeyType, ProductIdentifierListType>
{
    public static final String TAG = ProductIdentifierListCache.class.getSimpleName();

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
        for (Map.Entry<ProductIdentifierListKeyType, ProductIdentifierListType> entry : typedLists.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
    }
}
