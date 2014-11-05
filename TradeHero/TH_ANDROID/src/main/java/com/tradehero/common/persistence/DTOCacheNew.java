package com.tradehero.common.persistence;

import android.os.AsyncTask;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * See DTOKeyIdList to avoid duplicating data in caches.
 */
public interface DTOCacheNew<DTOKeyType extends DTOKey, DTOType extends DTO>
{
    public static final boolean DEFAULT_FORCE_UPDATE = false;

    boolean isValid(@NonNull DTOType value);

    @Nullable DTOType put(@NonNull DTOKeyType key, @NonNull DTOType value);

    @Nullable DTOType get(@NonNull DTOKeyType key);

    @NonNull DTOType fetch(@NonNull DTOKeyType key) throws Throwable;

    @NonNull DTOType getOrFetchSync(@NonNull DTOKeyType key) throws Throwable;

    @NonNull DTOType getOrFetchSync(@NonNull DTOKeyType key, boolean force) throws Throwable;

    void register(@NonNull DTOKeyType key, @Nullable Listener<DTOKeyType, DTOType> callback);

    void unregister(@NonNull DTOKeyType key, @Nullable Listener<DTOKeyType, DTOType> callback);

    void unregister(@Nullable Listener<DTOKeyType, DTOType> callback);

    void getOrFetchAsync(@NonNull DTOKeyType key);

    void getOrFetchAsync(@NonNull DTOKeyType key, boolean force);

    void invalidate(@NonNull DTOKeyType key);

    void invalidateAll();

    public static interface Listener<DTOKeyType, DTOType>
    {
        void onDTOReceived(@NonNull DTOKeyType key, @NonNull DTOType value);

        void onErrorThrown(@NonNull DTOKeyType key, @NonNull Throwable error);
    }

    public static interface HurriedListener<DTOKeyType, DTOType>
            extends Listener<DTOKeyType, DTOType>
    {
        void onPreCachedDTOReceived(@NonNull DTOKeyType key, @NonNull DTOType value);
    }

    abstract public static class CacheValue<DTOKeyType extends DTOKey, DTOType extends DTO>
    {
        @Nullable private DTOType value;
        @NonNull private final Set<Listener<DTOKeyType, DTOType>> listeners;
        @NonNull private final Set<HurriedListener<DTOKeyType, DTOType>> hurriedListeners;
        @NonNull protected WeakReference<GetOrFetchTask<DTOKeyType, DTOType>> fetchTask = new WeakReference<>(null);

        public CacheValue()
        {
            super();
            value = null;
            listeners = new HashSet<>();
            hurriedListeners = new HashSet<>();
            fetchTask = new WeakReference<>(null);
        }

        @Nullable public DTOType getValue()
        {
            return value;
        }

        public void setValue(@SuppressWarnings("NullableProblems") @NonNull DTOType value)
        {
            this.value = value;
        }

        public int getListenersCount()
        {
            return listeners.size();
        }

        public void registerListener(@NonNull Listener<DTOKeyType, DTOType> listener)
        {
            listeners.add(listener);
            if (listener instanceof HurriedListener)
            {
                hurriedListeners.add((HurriedListener<DTOKeyType, DTOType>) listener);
            }
        }

        public void unregisterListener(@NonNull Listener<DTOKeyType, DTOType> listener)
        {
            listeners.remove(listener);
            if (listener instanceof HurriedListener)
            {
                hurriedListeners.remove(listener);
            }
        }

        abstract public void getOrFetch(@NonNull final DTOKeyType key, boolean force);

        protected boolean needsRecreate(@Nullable GetOrFetchTask<DTOKeyType, DTOType> fetchTask)
        {
            return fetchTask == null || fetchTask.isCancelled() || fetchTask.getStatus() == AsyncTask.Status.FINISHED;
        }

        public void notifyHurriedListenersPreReceived(@NonNull DTOKeyType key, @NonNull DTOType value)
        {
            for (HurriedListener<DTOKeyType, DTOType> listener : new HashSet<>(hurriedListeners))
            {
                hurriedListeners.remove(listener);
                listener.onPreCachedDTOReceived(key, value);
            }
        }

        public void notifyListenersReceived(@NonNull DTOKeyType key, @NonNull DTOType value)
        {
            fetchTask = new WeakReference<>(null);
            for (Listener<DTOKeyType, DTOType> listener : new HashSet<>(listeners))
            {
                unregisterListener(listener);
                listener.onDTOReceived(key, value);
            }
        }

        public void notifyListenersFailed(@NonNull DTOKeyType key, @NonNull Throwable error)
        {
            fetchTask = new WeakReference<>(null);
            for (Listener<DTOKeyType, DTOType> listener : new HashSet<>(listeners))
            {
                unregisterListener(listener);
                listener.onErrorThrown(key, error);
            }
        }
    }

    abstract public static class GetOrFetchTask<DTOKeyType extends DTOKey, DTOType extends DTO>
            extends AsyncTask<Void, Void, DTOType>
    {
        @NonNull protected final DTOKeyType key;
        protected final boolean forceUpdateCache;

        //<editor-fold desc="Constructors">
        public GetOrFetchTask(@NonNull DTOKeyType key)
        {
            this(key, false);
        }

        public GetOrFetchTask(@NonNull DTOKeyType key, boolean forceUpdateCache)
        {
            super();
            this.key = key;
            this.forceUpdateCache = forceUpdateCache;
        }
        //</editor-fold>

        @NonNull abstract protected Class<?> getContainerCacheClass();

        public final AsyncTask<Void, Void, DTOType> execute()
        {
            return executePool();
        }

        protected AsyncTask<Void, Void, DTOType> executePool()
        {
            return executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        protected final AsyncTask<Void, Void, DTOType> executeSerial()
        {
            return executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }
}
