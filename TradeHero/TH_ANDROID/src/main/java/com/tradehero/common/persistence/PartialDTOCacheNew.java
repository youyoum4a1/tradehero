package com.tradehero.common.persistence;

import java.lang.ref.WeakReference;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

abstract public class PartialDTOCacheNew<DTOKeyType extends DTOKey, DTOType extends DTO>
        implements DTOCacheNew<DTOKeyType, DTOType>
{
    public static final int DEFAULT_AUTO_FETCH_TASK_MAX_SIZE = 50;

    public PartialDTOCacheNew()
    {
        super();
    }

    abstract protected CacheValue<DTOKeyType, DTOType> getCacheValue(DTOKeyType key);
    abstract protected void putCacheValue(DTOKeyType key, CacheValue<DTOKeyType, DTOType> cacheValue);

    protected CacheValue<DTOKeyType, DTOType> getOrCreateCacheValue(DTOKeyType key)
    {
        CacheValue<DTOKeyType, DTOType> cacheValue = getCacheValue(key);
        if (cacheValue == null)
        {
            cacheValue = createCacheValue(key);
            putCacheValue(key, cacheValue);
        }
        return cacheValue;
    }

    protected CacheValue<DTOKeyType, DTOType> createCacheValue(DTOKeyType key)
    {
        return new PartialCacheValue();
    }

    @Override public boolean isValid(@NotNull DTOType value)
    {
        if (value instanceof HasExpiration && ((HasExpiration) value).getExpiresInSeconds() <= 0)
        {
            return false;
        }
        return true;
    }

    @Override public DTOType put(@NotNull DTOKeyType key, @NotNull DTOType value)
    {
        CacheValue<DTOKeyType, DTOType> cacheValue = this.getOrCreateCacheValue(key);
        DTOType previous = cacheValue.getValue();
        cacheValue.setValue(value);
        return previous;
    }

    protected void checkKey(DTOKeyType key)
    {
        if (key == null)
        {
            throw new NullPointerException(String.format(
                    "Key cannot be null in cache %s",
                    getClass()));
        }
    }

    @Override public DTOType getOrFetchSync(DTOKeyType key) throws Throwable
    {
        return getOrFetchSync(key, DEFAULT_FORCE_UPDATE);
    }

    @Override public DTOType getOrFetchSync(DTOKeyType key, boolean force) throws Throwable
    {
        checkKey(key);
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

    @Override public void register(DTOKeyType key, Listener<DTOKeyType, DTOType> callback)
    {
        if (key != null)
        {
            // We need to create if needed because the user may ask later on.
            getOrCreateCacheValue(key).registerListener(callback);
        }
    }

    @Override public void unregister(DTOKeyType key, Listener<DTOKeyType, DTOType> callback)
    {
        if (key != null)
        {
            // We do not need to create here as this is to forget anyway
            CacheValue<DTOKeyType, DTOType> cacheValue = getCacheValue(key);
            if (cacheValue != null)
            {
                cacheValue.unregisterListener(callback);
            }
        }
    }

    @Override public void getOrFetchAsync(final DTOKeyType key)
    {
        getOrFetchAsync(key, DEFAULT_FORCE_UPDATE);
    }

    @Override public void getOrFetchAsync(final DTOKeyType key, final boolean forceUpdateCache)
    {
        checkKey(key);
        getOrCreateCacheValue(key).getOrFetch(key, forceUpdateCache);
    }

    protected void notifyHurriedListenersPreReceived(DTOKeyType key, DTOType value)
    {
        getOrCreateCacheValue(key).notifyHurriedListenersPreReceived(key, value);
    }

    protected void notifyListenersReceived(DTOKeyType key, DTOType value)
    {
        if (value == null)
        {
            Timber.e(new Exception(
                    String.format("Null value returned for key %s, on cache %s", key,
                            getCacheClass())), null);
        }
        getOrCreateCacheValue(key).notifyListenersReceived(key, value);
    }

    protected void notifyListenersFailed(DTOKeyType key, Throwable error)
    {
        getOrCreateCacheValue(key).notifyListenersFailed(key, error);
    }

    protected class PartialCacheValue extends CacheValue<DTOKeyType, DTOType>
    {
        public PartialCacheValue()
        {
            super();
        }

        public void getOrFetch(DTOKeyType key, boolean force)
        {
            GetOrFetchTask<DTOKeyType, DTOType> myFetchTask = fetchTask.get();
            if (needsRecreate(myFetchTask))
            {
                myFetchTask = createGetOrFetchTask(key, force);
                fetchTask = new WeakReference<>(myFetchTask);
                myFetchTask.execute();
            }
        }
    }

    protected PartialGetOrFetchTask createGetOrFetchTask(DTOKeyType key)
    {
        return createGetOrFetchTask(key, DEFAULT_FORCE_UPDATE);
    }

    protected PartialGetOrFetchTask createGetOrFetchTask(DTOKeyType key, boolean force)
    {
        return new PartialGetOrFetchTask(key, force);
    }

    protected class PartialGetOrFetchTask extends GetOrFetchTask<DTOKeyType, DTOType>
    {
        private Throwable error = null;

        //<editor-fold desc="Constructors">
        public PartialGetOrFetchTask(DTOKeyType key)
        {
            super(key);
        }

        public PartialGetOrFetchTask(DTOKeyType key, boolean forceUpdateCache)
        {
            super(key, forceUpdateCache);
        }
        //</editor-fold>

        @Override protected Class<?> getContainerCacheClass()
        {
            return getCacheClass();
        }

        @Override protected void onPreExecute()
        {
            super.onPreExecute();
            DTOType cached = PartialDTOCacheNew.this.get(key);
            if (cached != null)
            {
                notifyHurriedListenersPreReceived(key, cached);
            }
        }

        @Override protected DTOType doInBackground(Void... voids)
        {
            DTOType gotOrFetched = null;
            try
            {
                gotOrFetched = getOrFetchSync(key, forceUpdateCache);
            }
            catch (Throwable e)
            {
                error = e;
            }
            return gotOrFetched;
        }

        @Override protected void onPostExecute(DTOType value)
        {
            super.onPostExecute(value);

            if (isCancelled())
            {
                // Nothing to do
            }
            else if (error != null)
            {
                notifyListenersFailed(key, error);
            }
            else
            {
                notifyListenersReceived(key, value);
            }
        }
    }

    protected Class<?> getCacheClass()
    {
        return getClass();
    }
}
