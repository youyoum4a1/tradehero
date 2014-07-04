package com.tradehero.common.persistence;

import android.os.AsyncTask;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * See DTOKeyIdList to avoid duplicating data in caches.
 */
public interface DTOCacheNew<DTOKeyType extends DTOKey, DTOType extends DTO>
{
    public static final boolean DEFAULT_FORCE_UPDATE = false;

    boolean isValid(@NotNull DTOType value);
    @Nullable DTOType put(@NotNull DTOKeyType key, @NotNull DTOType value);
    /**
     * This method should be implemented so that it is very fast. Indeed this method is sometimes used before deciding
     * whether to getOrFetch
     * @param key
     * @return
     */
    @Nullable DTOType get(@NotNull DTOKeyType key);
    @NotNull DTOType fetch(@NotNull DTOKeyType key) throws Throwable;
    @NotNull DTOType getOrFetchSync(@NotNull DTOKeyType key) throws Throwable;
    @NotNull DTOType getOrFetchSync(@NotNull DTOKeyType key, boolean force) throws Throwable;
    void register(@NotNull DTOKeyType key, @Nullable Listener<DTOKeyType, DTOType> callback);
    void unregister(@NotNull DTOKeyType key, @Nullable Listener<DTOKeyType, DTOType> callback);
    void unregister(@Nullable Listener<DTOKeyType, DTOType> callback);
    void getOrFetchAsync(@NotNull DTOKeyType key);
    void getOrFetchAsync(@NotNull DTOKeyType key, boolean force);
    void invalidate(@NotNull DTOKeyType key);
    void invalidateAll();

    public static interface Listener<DTOKeyType, DTOType>
    {
        void onDTOReceived(@NotNull DTOKeyType key, @NotNull DTOType value);
        void onErrorThrown(@NotNull DTOKeyType key, @NotNull Throwable error);
    }

    public static interface HurriedListener<DTOKeyType, DTOType>
            extends Listener<DTOKeyType, DTOType>
    {
        void onPreCachedDTOReceived(@NotNull DTOKeyType key, @NotNull DTOType value);
    }

    abstract public static class CacheValue<DTOKeyType extends DTOKey, DTOType extends DTO>
    {
        @Nullable private DTOType value;
        @NotNull private final Set<Listener<DTOKeyType, DTOType>> listeners;
        @NotNull protected WeakReference<GetOrFetchTask<DTOKeyType, DTOType>> fetchTask = new WeakReference<>(null);

        public CacheValue()
        {
            super();
            value = null;
            listeners = new HashSet<>();
            fetchTask = new WeakReference<>(null);
        }

        @Nullable public DTOType getValue()
        {
            return value;
        }

        public void setValue(@NotNull DTOType value)
        {
            this.value = value;
        }

        public int getListenersCount()
        {
            return listeners.size();
        }

        public void registerListener(@NotNull Listener<DTOKeyType, DTOType> listener)
        {
            listeners.add(listener);
        }

        public void unregisterListener(@NotNull Listener<DTOKeyType, DTOType> listener)
        {
            listeners.remove(listener);
        }

        abstract public void getOrFetch(@NotNull final DTOKeyType key, boolean force);

        protected boolean needsRecreate(@Nullable GetOrFetchTask<DTOKeyType, DTOType> fetchTask)
        {
            return fetchTask == null || fetchTask.isCancelled() || fetchTask.getStatus() == AsyncTask.Status.FINISHED;
        }

        public void notifyHurriedListenersPreReceived(@NotNull DTOKeyType key, @NotNull DTOType value)
        {
            for (@NotNull Listener<DTOKeyType, DTOType> listener : new HashSet<>(listeners))
            {
                if (listener instanceof HurriedListener)
                {
                    ((HurriedListener<DTOKeyType, DTOType>) listener)
                            .onPreCachedDTOReceived(key, value);
                }
            }
        }

        public void notifyListenersReceived(@NotNull DTOKeyType key, @NotNull DTOType value)
        {
            fetchTask = new WeakReference<>(null);
            for (@NotNull Listener<DTOKeyType, DTOType> listener : new HashSet<>(listeners))
            {
                listener.onDTOReceived(key, value);
                unregisterListener(listener);
            }
        }

        public void notifyListenersFailed(@NotNull DTOKeyType key, @NotNull Throwable error)
        {
            fetchTask = new WeakReference<>(null);
            for (Listener<DTOKeyType, DTOType> listener : new HashSet<>(listeners))
            {
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
        @NotNull protected final DTOKeyType key;
        protected final boolean forceUpdateCache;

        //<editor-fold desc="Constructors">
        public GetOrFetchTask(@NotNull DTOKeyType key)
        {
            this(key, false);
        }

        public GetOrFetchTask(@NotNull DTOKeyType key, boolean forceUpdateCache)
        {
            super();
            this.key = key;
            this.forceUpdateCache = forceUpdateCache;
        }
        //</editor-fold>

        @NotNull abstract protected Class<?> getContainerCacheClass();

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
