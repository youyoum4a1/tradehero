package com.tradehero.common.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class StraightDTOCache<DTOKeyType extends DTOKey, DTOType extends DTO>
        extends PartialDTOCache<DTOKeyType, DTOType>
{
    final private THLruCache<DTOKeyType, DTOType> lruCache;

    //<editor-fold desc="Constructors">
    public StraightDTOCache(int maxSize)
    {
        super();
        this.lruCache = new THLruCache<>(maxSize);
    }
    //</editor-fold>

    @Contract("null -> null; !null -> _")
    @Nullable
    @Override public DTOType get(@Nullable DTOKeyType key)
    {
        if (key == null)
        {
            return null;
        }
        DTOType value = this.lruCache.get(key);
        if (value instanceof HasExpiration && ((HasExpiration) value).getExpiresInSeconds() <= 0)
        {
            invalidate(key);
            return null;
        }
        return value;
    }

    @Nullable
    @Override public DTOType put(DTOKeyType key, DTOType value)
    {
        return this.lruCache.put(key, value);
    }

    @Override public void invalidate(DTOKeyType key)
    {
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    @NotNull protected Map<DTOKeyType, DTOType> snapshot()
    {
        return lruCache.snapshot();
    }

    @NotNull public List<DTOKeyType> getAllKeys()
    {
        return new ArrayList<>(snapshot().keySet());
    }
}
