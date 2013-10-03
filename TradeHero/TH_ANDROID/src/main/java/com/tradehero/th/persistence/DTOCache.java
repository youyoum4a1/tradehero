package com.tradehero.th.persistence;

import com.tradehero.th.api.DTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:48 PM To change this template use File | Settings | File Templates. */
public interface DTOCache<BaseKeyType, KeyType extends DTOKey<BaseKeyType>, DTOType>
{
    DTOType get(KeyType key);
    DTOType getOrFetch(KeyType key, boolean force);
    DTOType put(KeyType key, DTOType value);
}
