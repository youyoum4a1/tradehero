package com.tradehero.common.persistence;

import android.os.AsyncTask;

/**
 * See DTOKeyIdList to avoid duplicating data in caches.
 */
public interface DTOCache<DTOKeyType extends DTOKey, DTOType extends DTO>
{
    /**
     * This method should be implemented so that it is very fast. Indeed this method is sometimes used before deciding
     * whether to getOrFetch
     * @param key
     * @return
     */
    DTOType get(DTOKeyType key);

    DTOType getOrFetch(DTOKeyType key)  throws Throwable;
    DTOType getOrFetch(DTOKeyType key, boolean force)  throws Throwable;
    GetOrFetchTask<DTOKeyType, DTOType> getOrFetch(DTOKeyType key, Listener<DTOKeyType, DTOType> callback);
    GetOrFetchTask<DTOKeyType, DTOType> getOrFetch(DTOKeyType key, boolean force, Listener<DTOKeyType, DTOType> callback);
    DTOType put(DTOKeyType key, DTOType value);
    void invalidate(DTOKeyType key);
    void invalidateAll();

    public static interface Listener<DTOKeyType, DTOType>
    {
        void onDTOReceived(DTOKeyType key, DTOType value, boolean fromCache);
        void onErrorThrown(DTOKeyType key, Throwable error);
    }

    /**
     * The advantage of this class over simply a cancellable AsyncTask is that implementations can let the fetch run its course,
     * which includes caching, but stop just before calling the listener.
     * @param <DTOKeyType>
     * @param <DTOType>
     */
    abstract public static class GetOrFetchTask<DTOKeyType, DTOType> extends AsyncTask<Void, Void, DTOType>
    {
        private Listener<DTOKeyType, DTOType> listener;

        public GetOrFetchTask()
        {
            this(null);
        }

        public GetOrFetchTask(Listener<DTOKeyType, DTOType> listener)
        {
            super();
            this.listener = listener;
        }

        /**
         * The listener should be strongly referenced elsewhere.
         * @param listener
         */
        public void setListener(Listener<DTOKeyType, DTOType> listener)
        {
            this.listener = listener;
        }

        public Listener<DTOKeyType, DTOType> getListener()
        {
            return this.listener;
        }
    }
}
