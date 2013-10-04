package com.tradehero.th.persistence;

import android.support.v4.util.LruCache;
import com.tradehero.th.api.DTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/4/13 Time: 11:01 AM To change this template use File | Settings | File Templates. */
abstract public class StraightDTOCache<BaseKeyType, DTOKeyType extends DTOKey<BaseKeyType>, DTOType> implements DTOCache<BaseKeyType, DTOKeyType, DTOType>
{
    private LruCache<BaseKeyType, DTOType> lruCache;

    public StraightDTOCache(int maxSize)
    {
        super();
        this.lruCache = new LruCache<>(maxSize);
    }
    
    abstract protected DTOType fetch(DTOKeyType key);

    @Override public DTOType get(DTOKeyType key)
    {
        return this.lruCache.get(key.makeKey());
    }

    @Override public DTOType put(DTOKeyType key, DTOType value)
    {
        return this.lruCache.put(key.makeKey(), value);
    }

    public DTOType getOrFetch(DTOKeyType key, boolean force)
    {
        DTOType value = get(key);

        if (force || value == null)
        {
            value = fetch(key);

            if (value != null)
            {
                put(key, value);
            }
        }

        return value;
    }
}
