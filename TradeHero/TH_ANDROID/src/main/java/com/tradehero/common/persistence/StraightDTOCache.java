package com.tradehero.common.persistence;

import android.support.v4.util.LruCache;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/4/13 Time: 11:01 AM To change this template use File | Settings | File Templates.
 *
 */
abstract public class StraightDTOCache<DTOKeyType extends DTOKey, DTOType extends DTO>
        extends PartialDTOCache<DTOKeyType, DTOType>
{
    private LruCache<DTOKeyType, DTOType> lruCache;

    public StraightDTOCache(int maxSize)
    {
        super();
        this.lruCache = new LruCache<>(maxSize);
    }
    
    @Override public DTOType get(DTOKeyType key)
    {
        if (key == null)
        {
            return null;
        }
        return this.lruCache.get(key);
    }

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
}
