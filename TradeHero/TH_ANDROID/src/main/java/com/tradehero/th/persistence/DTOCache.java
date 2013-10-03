package com.tradehero.th.persistence;

import com.tradehero.th.api.DTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:48 PM To change this template use File | Settings | File Templates. */
public interface DTOCache<BaseKeyType, DTOKeyType extends DTOKey<BaseKeyType>, DTOType>
{
    DTOType get(DTOKeyType key);
    DTOType getOrFetch(DTOKeyType key, boolean force);
    DTOType put(DTOKeyType key, DTOType value);
}
