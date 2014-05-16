package com.tradehero.common.persistence;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * The registered listeners are kept as weak references. So they should be strongly referenced elsewhere.
 */
abstract public class PartialDTOCache<DTOKeyType extends DTOKey, DTOType extends DTO>
        implements LiveDTOCache<DTOKeyType, DTOType>
{
    public static final int DEFAULT_AUTO_FETCH_TASK_MAX_SIZE = 50;

    private List<WeakReference<Listener<DTOKeyType, DTOType>>> listeners;
    private THLruCache<DTOKeyType, GetOrFetchTask<DTOKeyType, DTOType>> autoFetchTasks = new THLruCache<>(DEFAULT_AUTO_FETCH_TASK_MAX_SIZE);

    public PartialDTOCache()
    {
        super();
        this.listeners = new ArrayList<>();
    }

    abstract protected DTOType fetch(DTOKeyType key) throws Throwable;

    @Override public DTOType getOrFetch(DTOKeyType key) throws Throwable
    {
        return getOrFetch(key, false);
    }

    @Override public DTOType getOrFetch(DTOKeyType key, boolean force) throws Throwable
    {
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

    @Override public GetOrFetchTask<DTOKeyType, DTOType> getOrFetch(final DTOKeyType key, final Listener<DTOKeyType, DTOType> callback)
    {
        return getOrFetch(key, false, callback);
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param key
     * @param forceUpdateCache
     * @param initialListener
     * @return
     */
    @Override public GetOrFetchTask<DTOKeyType, DTOType> getOrFetch(
            final DTOKeyType key, final boolean forceUpdateCache, final Listener<DTOKeyType, DTOType> initialListener)
    {
        return new GetOrFetchTask<DTOKeyType, DTOType>(initialListener)
        {
            private Throwable error = null;
            private boolean shouldNotifyListenerOnCacheUpdated = true;

            long start = 0;
            @Override protected void onPreExecute()
            {
                DTOType cached = PartialDTOCache.this.get(key);
                Listener<DTOKeyType, DTOType> currentListener = getListener();
                if (cached != null && currentListener != null)
                {
                    currentListener.onDTOReceived(key, cached, true);
                    shouldNotifyListenerOnCacheUpdated = forceUpdateCache;
                }
                start = System.currentTimeMillis();
            }

            @Override protected DTOType doInBackground(Void... voids)
            {
                DTOType gotOrFetched = null;
                try
                {
                    gotOrFetched = getOrFetch(key, forceUpdateCache);
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
                if (!isCancelled())
                {
                    // We retrieve the callback right away to avoid having it vanish between the 2 usages.
                    Listener<DTOKeyType, DTOType> currentListener = getListener();
                    if (currentListener != null)
                    {
                        if (error != null)
                        {
                            currentListener.onErrorThrown(key, error);
                        }
                        // not to notify listener about data come from cache again
                        else if (shouldNotifyListenerOnCacheUpdated)
                        {
                            if (value == null)
                            {
                                Timber.e(new Exception("Null value returned for key " + key), "");
                            }
                            currentListener.onDTOReceived(key, value, !forceUpdateCache);
                        }
                    }

                    if (error == null)
                    {
                        pushToListeners(key);
                    }
                }
            }
        };
    }

    @Override public boolean isListenerRegistered(Listener<DTOKeyType, DTOType> listener)
    {
        List<WeakReference<Listener<DTOKeyType, DTOType>>> lostListeners = new ArrayList<>();
        boolean alreadyIn = false;

        for (WeakReference<Listener<DTOKeyType, DTOType>> weakListener: listeners)
        {
            Listener<DTOKeyType, DTOType> knownListener = weakListener.get();
            if (knownListener == null)
            {
                lostListeners.add(weakListener);
            }
            else if (knownListener == listener)
            {
                alreadyIn = true;
                break;
            }
        }

        removeListeners(lostListeners);

        return alreadyIn;
    }

    @Override public void registerListener(Listener<DTOKeyType, DTOType> listener)
    {
        if (!isListenerRegistered(listener))
        {
            listeners.add(new WeakReference<>(listener));
        }
    }

    @Override public void unRegisterListener(Listener<DTOKeyType, DTOType> listener)
    {
        List<WeakReference<Listener<DTOKeyType, DTOType>>> lostListeners = new ArrayList<>();

        for (WeakReference<Listener<DTOKeyType, DTOType>> weakListener: listeners)
        {
            Listener<DTOKeyType, DTOType> knownListener = weakListener.get();
            if (knownListener == null)
            {
                lostListeners.add(weakListener);
            }
            else if (knownListener == listener)
            {
                listeners.remove(listener);
                break;
            }
        }

        removeListeners(lostListeners);
    }

    @Override public void pushToListeners(DTOKeyType key)
    {
        List<WeakReference<Listener<DTOKeyType, DTOType>>> lostListeners = new ArrayList<>();

        for (WeakReference<Listener<DTOKeyType, DTOType>> weakListener: listeners)
        {
            Listener<DTOKeyType, DTOType> knownListener = weakListener.get();
            if (knownListener == null)
            {
                lostListeners.add(weakListener);
            }
            else
            {
                knownListener.onDTOReceived(key, get(key), false);
            }
        }

        removeListeners(lostListeners);
    }

    private void removeListeners(List<WeakReference<Listener<DTOKeyType, DTOType>>> lostListeners)
    {
        for (WeakReference<Listener<DTOKeyType, DTOType>> lostListener: lostListeners)
        {
            listeners.remove(lostListener);
        }
    }

    public void autoFetch(DTOKeyType key)
    {
        autoFetch(key, false);
    }

    /**
     * This helps you prefetch stuff you don't want to wait to fetch later on.
     * @param key
     */
    public void autoFetch(DTOKeyType key, boolean force)
    {
        if (!force && (key == null || get(key) != null))
        {
            return;
        }

        GetOrFetchTask<DTOKeyType, DTOType> fetchTask = getOrFetch(key, force, null);
        autoFetchTasks.put(key, fetchTask);
        fetchTask.execute();
    }

    public void autoFetch(List<? extends DTOKeyType> keys, DTOKeyType typeQualifier)
    {
        autoFetch(keys, false, typeQualifier);
    }

    /**
     * Beware that if you submit more keys than the DEFAULT limit, then only the last ones will get in.
     * @param keys
     */
    public void autoFetch(List<? extends DTOKeyType> keys, boolean force, DTOKeyType typeQualifier)
    {
        if (keys == null)
        {
            return;
        }
        for (DTOKeyType key: keys)
        {
            autoFetch(key, force);
        }
    }
}
