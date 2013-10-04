package com.tradehero.common.persistence;

import android.os.AsyncTask;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:48 PM To change this template use File | Settings | File Templates. */
public interface DTOCache<BaseKeyType, DTOKeyType extends DTOKey<BaseKeyType>, DTOType>
{
    DTOType get(DTOKeyType key);
    DTOType getOrFetch(DTOKeyType key, boolean force);
    AsyncTask<Void, Void, DTOType> getOrFetch(DTOKeyType key, boolean force, Listener<DTOKeyType, DTOType> callback);
    DTOType put(DTOKeyType key, DTOType value);

    public static interface Listener<DTOKeyType, DTOType>
    {
        void onDTOReceived(DTOKeyType key, DTOType value);
    }
}
