package com.tradehero.common.billing;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;

import android.support.annotation.NonNull;

import java.util.Map;

import timber.log.Timber;

abstract public class ProductIdentifierListCache<
            ProductIdentifierType extends ProductIdentifier,
            ProductIdentifierListKeyType extends ProductIdentifierListKey,
            ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>>
        extends StraightDTOCacheNew<ProductIdentifierListKeyType, ProductIdentifierListType>
{
    //<editor-fold desc="Constructors">
    public ProductIdentifierListCache(int maxSize,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(maxSize, dtoCacheUtil);
    }
    //</editor-fold>

    @Override @NonNull public ProductIdentifierListType fetch(@NonNull ProductIdentifierListKeyType key) throws Throwable
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

    @Override public ProductIdentifierListType put(
            @NonNull ProductIdentifierListKeyType key,
            @NonNull ProductIdentifierListType value)
    {
        Timber.d("Put %s", key);
        return super.put(key, value);
    }
}
