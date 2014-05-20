package com.tradehero.common.persistence;

import android.os.AsyncTask;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

/**
 * See DTOKeyIdList to avoid duplicating data in caches.
 */
public interface DTOCacheNew<DTOKeyType extends DTOKey, DTOType extends DTO>
{
    public static final boolean DEFAULT_FORCE_UPDATE = false;

    DTOType put(DTOKeyType key, DTOType value);
    /**
     * This method should be implemented so that it is very fast. Indeed this method is sometimes used before deciding
     * whether to getOrFetch
     * @param key
     * @return
     */
    DTOType get(DTOKeyType key);
    DTOType fetch(DTOKeyType key) throws Throwable;
    DTOType getOrFetchSync(DTOKeyType key) throws Throwable;
    DTOType getOrFetchSync(DTOKeyType key, boolean force) throws Throwable;
    void register(DTOKeyType key, Listener<DTOKeyType, DTOType> callback);
    void unregister(DTOKeyType key, Listener<DTOKeyType, DTOType> callback);
    void unregister(Listener<DTOKeyType, DTOType> callback);
    void getOrFetchAsync(DTOKeyType key);
    void getOrFetchAsync(DTOKeyType key, boolean force);
    void invalidate(DTOKeyType key);
    void invalidateAll();

    public static interface Listener<DTOKeyType, DTOType>
    {
        void onDTOReceived(DTOKeyType key, DTOType value, boolean fromCache);
        void onErrorThrown(DTOKeyType key, Throwable error);
    }

    abstract public static class CacheValue<DTOKeyType extends DTOKey, DTOType extends DTO>
    {
        private DTOType value;
        private Set<Listener<DTOKeyType, DTOType>> listeners;
        protected WeakReference<GetOrFetchTask<DTOKeyType, DTOType>> fetchTask = new WeakReference<>(null);

        public CacheValue()
        {
            super();
            value = null;
            listeners = new HashSet<>();
            fetchTask = new WeakReference<>(null);
        }

        public DTOType getValue()
        {
            return value;
        }

        public void setValue(DTOType value)
        {
            this.value = value;
        }

        public void registerListener(Listener<DTOKeyType, DTOType> listener)
        {
            listeners.add(listener);
        }

        public void unregisterListener(Listener<DTOKeyType, DTOType> listener)
        {
            listeners.remove(listener);
        }

        abstract public void getOrFetch(final DTOKeyType key, boolean force);

        protected boolean needsRecreate(GetOrFetchTask<DTOKeyType, DTOType> fetchTask)
        {
            return fetchTask == null || fetchTask.isCancelled() || fetchTask.getStatus() == AsyncTask.Status.FINISHED;
        }

        public void notifyListenersReceived(DTOKeyType key, DTOType value, boolean fromCache)
        {
            Listener<DTOKeyType, DTOType> listener;
            for (Listener<DTOKeyType, DTOType> dtoKeyTypeDTOTypeListener : new HashSet<>(listeners))
            {
                listener = dtoKeyTypeDTOTypeListener;
                if (listener != null)
                {
                    listener.onDTOReceived(key, value, fromCache);
                }
                //unregisterListener(listener);
            }
        }

        public void notifyListenersFailed(DTOKeyType key, Throwable error)
        {
            Listener<DTOKeyType, DTOType> listener;
            for (Listener<DTOKeyType, DTOType> dtoKeyTypeDTOTypeListener : new HashSet<>(listeners))
            {
                listener = dtoKeyTypeDTOTypeListener;
                if (listener != null)
                {
                    listener.onErrorThrown(key, error);
                }
                unregisterListener(listener);
            }
        }
    }

    abstract public static class GetOrFetchTask<DTOKeyType extends DTOKey, DTOType extends DTO>
            extends AsyncTask<Void, Void, DTOType>
    {
        protected DTOKeyType key;
        protected boolean forceUpdateCache;

        public GetOrFetchTask(DTOKeyType key)
        {
            this(key, false);
        }

        public GetOrFetchTask(DTOKeyType key, boolean forceUpdateCache)
        {
            super();
            this.key = key;
            this.forceUpdateCache = forceUpdateCache;
        }
    }
}
