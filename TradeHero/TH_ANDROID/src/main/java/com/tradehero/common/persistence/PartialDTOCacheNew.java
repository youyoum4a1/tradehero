package com.tradehero.common.persistence;

import java.lang.ref.WeakReference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

abstract public class PartialDTOCacheNew<DTOKeyType extends DTOKey, DTOType extends DTO>
        implements DTOCacheNew<DTOKeyType, DTOType>
{
    //<editor-fold desc="Constructors">
    public PartialDTOCacheNew(@NonNull DTOCacheUtilNew dtoCacheUtilNew)
    {
        super();
        dtoCacheUtilNew.addCache(this);
    }
    //</editor-fold>

    @Nullable abstract protected CacheValue<DTOKeyType, DTOType> getCacheValue(@NonNull DTOKeyType key);
    abstract protected void putCacheValue(@NonNull DTOKeyType key, @NonNull CacheValue<DTOKeyType, DTOType> cacheValue);

    @NonNull protected CacheValue<DTOKeyType, DTOType> getOrCreateCacheValue(@NonNull DTOKeyType key)
    {
        CacheValue<DTOKeyType, DTOType> cacheValue = getCacheValue(key);
        if (cacheValue == null)
        {
            cacheValue = createCacheValue(key);
            putCacheValue(key, cacheValue);
        }
        return cacheValue;
    }

    @NonNull protected CacheValue<DTOKeyType, DTOType> createCacheValue(@NonNull DTOKeyType key)
    {
        return new PartialCacheValue();
    }

    @Override public boolean isValid(@NonNull DTOType value)
    {
        //noinspection RedundantIfStatement
        if (value instanceof HasExpiration && ((HasExpiration) value).getExpiresInSeconds() <= 0)
        {
            return false;
        }
        return true;
    }

    @Override @Nullable public DTOType put(@NonNull DTOKeyType key, @NonNull DTOType value)
    {
        CacheValue<DTOKeyType, DTOType> cacheValue = this.getCacheValue(key);
        DTOType previous = null;
        if (!isValid(value))
        {
            // We do not bother creating a CacheValue for an invalid value
            if (cacheValue != null)
            {
                previous = cacheValue.getValue();
            }
        }
        else
        {
            cacheValue = this.getOrCreateCacheValue(key);
            previous = cacheValue.getValue();
            cacheValue.setValue(value);
        }
        return previous;
    }

    @Override @Nullable public DTOType get(@NonNull DTOKeyType key)
    {
        CacheValue<DTOKeyType, DTOType> cacheValue = getCacheValue(key);
        if (cacheValue == null)
        {
            return null;
        }
        DTOType value = cacheValue.getValue();
        if (value != null && !isValid(value))
        {
            if (cacheValue.getListenersCount() == 0)
            {
                invalidate(key);
            }
            return null;
        }
        return value;
    }

    @Override @NonNull public DTOType getOrFetchSync(@NonNull DTOKeyType key) throws Throwable
    {
        return getOrFetchSync(key, DEFAULT_FORCE_UPDATE);
    }

    @Override @NonNull public DTOType getOrFetchSync(@NonNull DTOKeyType key, boolean force) throws Throwable
    {
        DTOType value = get(key);

        if (force || value == null)
        {
            value = fetch(key);
            put(key, value);
        }

        return value;
    }

    @Override public void register(@NonNull DTOKeyType key, @Nullable Listener<DTOKeyType, DTOType> callback)
    {
        if (callback != null)
        {
            CacheValue<DTOKeyType, DTOType> cacheValue = getOrCreateCacheValue(key);
            cacheValue.registerListener(callback);
        }
    }

    @Override public void unregister(@NonNull DTOKeyType key, @Nullable Listener<DTOKeyType, DTOType> callback)
    {
        // We do not need to create here as this is to forget anyway
        CacheValue<DTOKeyType, DTOType> cacheValue = getCacheValue(key);
        if (callback != null && cacheValue != null)
        {
            cacheValue.unregisterListener(callback);
        }
    }

    @Override public void getOrFetchAsync(@NonNull final DTOKeyType key)
    {
        getOrFetchAsync(key, DEFAULT_FORCE_UPDATE);
    }

    @Override public void getOrFetchAsync(@NonNull final DTOKeyType key, final boolean forceUpdateCache)
    {
        DTOType cached = get(key);
        if (cached != null)
        {
            notifyHurriedListenersPreReceived(key, cached);
        }

        getOrCreateCacheValue(key).getOrFetch(key, forceUpdateCache);
    }

    protected void notifyHurriedListenersPreReceived(@NonNull DTOKeyType key, @NonNull DTOType value)
    {
        getOrCreateCacheValue(key).notifyHurriedListenersPreReceived(key, value);
    }

    protected void notifyListenersReceived(@NonNull DTOKeyType key, @NonNull DTOType value)
    {
        getOrCreateCacheValue(key).notifyListenersReceived(key, value);
    }

    protected void notifyListenersFailed(@NonNull DTOKeyType key, @NonNull Throwable error)
    {
        getOrCreateCacheValue(key).notifyListenersFailed(key, error);
    }

    protected class PartialCacheValue extends CacheValue<DTOKeyType, DTOType>
    {
        //<editor-fold desc="Constructors">
        public PartialCacheValue()
        {
            super();
        }
        //</editor-fold>

        @Override public void getOrFetch(@NonNull DTOKeyType key, boolean force)
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

    @NonNull protected PartialGetOrFetchTask createGetOrFetchTask(@NonNull DTOKeyType key)
    {
        return createGetOrFetchTask(key, DEFAULT_FORCE_UPDATE);
    }

    @NonNull protected PartialGetOrFetchTask createGetOrFetchTask(@NonNull DTOKeyType key, boolean force)
    {
        return new PartialGetOrFetchTask(key, force);
    }

    protected class PartialGetOrFetchTask extends GetOrFetchTask<DTOKeyType, DTOType>
    {
        @Nullable private Throwable error = null;

        //<editor-fold desc="Constructors">
        public PartialGetOrFetchTask(@NonNull DTOKeyType key)
        {
            super(key);
        }

        public PartialGetOrFetchTask(@NonNull DTOKeyType key, boolean forceUpdateCache)
        {
            super(key, forceUpdateCache);
        }
        //</editor-fold>

        @Override @NonNull protected Class<?> getContainerCacheClass()
        {
            return getCacheClass();
        }

        @Override @Nullable protected DTOType doInBackground(Void... voids)
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

        @Override protected void onPostExecute(@Nullable DTOType value)
        {
            super.onPostExecute(value);

            //noinspection StatementWithEmptyBody
            if (isCancelled())
            {
                // Nothing to do
            }
            else if (error != null)
            {
                notifyListenersFailed(key, error);
            }
            else if (value == null)
            {
                notifyListenersFailed(key, new NullPointerException(String.format("Null value returned for %s in cache %s", key, getClass())));
            }
            else
            {
                notifyListenersReceived(key, value);
            }
        }
    }

    @NonNull protected Class<?> getCacheClass()
    {
        return getClass();
    }
}
