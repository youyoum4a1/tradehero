package com.tradehero.common.persistence;

import java.lang.ref.WeakReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class PartialDTOCacheNew<DTOKeyType extends DTOKey, DTOType extends DTO>
        implements DTOCacheNew<DTOKeyType, DTOType>
{
    //<editor-fold desc="Constructors">
    public PartialDTOCacheNew()
    {
        super();
    }
    //</editor-fold>

    @Nullable abstract protected CacheValue<DTOKeyType, DTOType> getCacheValue(@NotNull DTOKeyType key);
    abstract protected void putCacheValue(@NotNull DTOKeyType key, @NotNull CacheValue<DTOKeyType, DTOType> cacheValue);

    @NotNull protected CacheValue<DTOKeyType, DTOType> getOrCreateCacheValue(@NotNull DTOKeyType key)
    {
        @Nullable CacheValue<DTOKeyType, DTOType> cacheValue = getCacheValue(key);
        if (cacheValue == null)
        {
            cacheValue = createCacheValue(key);
            putCacheValue(key, cacheValue);
        }
        return cacheValue;
    }

    @NotNull protected CacheValue<DTOKeyType, DTOType> createCacheValue(@NotNull DTOKeyType key)
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

    @Override @Nullable public DTOType put(@NotNull DTOKeyType key, @NotNull DTOType value)
    {
        @Nullable CacheValue<DTOKeyType, DTOType> cacheValue = this.getCacheValue(key);
        @Nullable DTOType previous = null;
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

    @Override @NotNull public DTOType getOrFetchSync(@NotNull DTOKeyType key) throws Throwable
    {
        return getOrFetchSync(key, DEFAULT_FORCE_UPDATE);
    }

    @Override @NotNull public DTOType getOrFetchSync(@NotNull DTOKeyType key, boolean force) throws Throwable
    {
        @Nullable DTOType value = get(key);

        if (force || value == null)
        {
            value = fetch(key);
            put(key, value);
        }

        return value;
    }

    @Override public void register(@NotNull DTOKeyType key, @Nullable Listener<DTOKeyType, DTOType> callback)
    {
        if (callback != null)
        {
            getOrCreateCacheValue(key).registerListener(callback);
        }
    }

    @Override public void unregister(@NotNull DTOKeyType key, @Nullable Listener<DTOKeyType, DTOType> callback)
    {
        // We do not need to create here as this is to forget anyway
        @Nullable CacheValue<DTOKeyType, DTOType> cacheValue = getCacheValue(key);
        if (callback != null && cacheValue != null)
        {
            cacheValue.unregisterListener(callback);
        }
    }

    @Override public void getOrFetchAsync(@NotNull final DTOKeyType key)
    {
        getOrFetchAsync(key, DEFAULT_FORCE_UPDATE);
    }

    @Override public void getOrFetchAsync(@NotNull final DTOKeyType key, final boolean forceUpdateCache)
    {
        getOrCreateCacheValue(key).getOrFetch(key, forceUpdateCache);
    }

    protected void notifyHurriedListenersPreReceived(@NotNull DTOKeyType key, @NotNull DTOType value)
    {
        getOrCreateCacheValue(key).notifyHurriedListenersPreReceived(key, value);
    }

    protected void notifyListenersReceived(@NotNull DTOKeyType key, @NotNull DTOType value)
    {
        getOrCreateCacheValue(key).notifyListenersReceived(key, value);
    }

    protected void notifyListenersFailed(@NotNull DTOKeyType key, @NotNull Throwable error)
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

        public void getOrFetch(@NotNull DTOKeyType key, boolean force)
        {
            @Nullable GetOrFetchTask<DTOKeyType, DTOType> myFetchTask = fetchTask.get();
            if (needsRecreate(myFetchTask))
            {
                myFetchTask = createGetOrFetchTask(key, force);
                fetchTask = new WeakReference<>(myFetchTask);
                myFetchTask.execute();
            }
        }
    }

    @NotNull protected PartialGetOrFetchTask createGetOrFetchTask(@NotNull DTOKeyType key)
    {
        return createGetOrFetchTask(key, DEFAULT_FORCE_UPDATE);
    }

    @NotNull protected PartialGetOrFetchTask createGetOrFetchTask(@NotNull DTOKeyType key, boolean force)
    {
        return new PartialGetOrFetchTask(key, force);
    }

    protected class PartialGetOrFetchTask extends GetOrFetchTask<DTOKeyType, DTOType>
    {
        @Nullable private Throwable error = null;

        //<editor-fold desc="Constructors">
        public PartialGetOrFetchTask(@NotNull DTOKeyType key)
        {
            super(key);
        }

        public PartialGetOrFetchTask(@NotNull DTOKeyType key, boolean forceUpdateCache)
        {
            super(key, forceUpdateCache);
        }
        //</editor-fold>

        @Override @NotNull protected Class<?> getContainerCacheClass()
        {
            return getCacheClass();
        }

        @Override protected void onPreExecute()
        {
            super.onPreExecute();
            @Nullable DTOType cached = PartialDTOCacheNew.this.get(key);
            if (cached != null)
            {
                notifyHurriedListenersPreReceived(key, cached);
            }
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

    @NotNull protected Class<?> getCacheClass()
    {
        return getClass();
    }
}
