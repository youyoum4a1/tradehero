package com.tradehero.common.persistence;

/**
 * Provides for a way to be informed on all updates to the cache
 * */
public interface LiveDTOCache<DTOKeyType extends DTOKey, DTOType extends DTO>
        extends DTOCache<DTOKeyType, DTOType>
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
