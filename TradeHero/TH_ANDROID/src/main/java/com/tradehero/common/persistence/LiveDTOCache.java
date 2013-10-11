package com.tradehero.common.persistence;

/** Created with IntelliJ IDEA. User: xavier Date: 10/11/13 Time: 5:55 PM To change this template use File | Settings | File Templates. */
public interface LiveDTOCache<BaseKeyType, DTOKeyType extends DTOKey<BaseKeyType>, DTOType>
        extends DTOCache<BaseKeyType, DTOKeyType, DTOType>
{
    boolean isListenerRegistered(Listener<DTOKeyType, DTOType> listener);
    void registerListener(Listener<DTOKeyType, DTOType> listener);
    void unRegisterListener(Listener<DTOKeyType, DTOType> listener);

    /**
     * This method pushes null if the key is not present.
     * It is preferable to call this method from the UI thread.
     * @param key
     */
    void pushToListeners(DTOKeyType key);
}
