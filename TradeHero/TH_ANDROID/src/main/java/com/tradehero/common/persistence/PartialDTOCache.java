package com.tradehero.common.persistence;

import android.support.v4.util.LruCache;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * The registered listeners are kept as weak references. So they should be strongly referenced elsewhere.
 * Created with IntelliJ IDEA. User: xavier Date: 10/24/13 Time: 4:51 PM To change this template use File | Settings | File Templates.
 */
abstract public class PartialDTOCache<DTOKeyType extends DTOKey, DTOType extends DTO>
        implements LiveDTOCache<DTOKeyType, DTOType>
{
    public static final String TAG = PartialDTOCache.class.getSimpleName();
    public static final int DEFAULT_AUTO_FETCH_TASK_MAX_SIZE = 50;

    private List<WeakReference<Listener<DTOKeyType, DTOType>>> listeners;
    private LruCache<DTOKeyType, GetOrFetchTask<DTOKeyType, DTOType>> autoFetchTasks = new LruCache<>(DEFAULT_AUTO_FETCH_TASK_MAX_SIZE);

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
     * @param force
     * @param callback
     * @return
     */
    @Override public GetOrFetchTask<DTOKeyType, DTOType> getOrFetch(final DTOKeyType key, final boolean force, final Listener<DTOKeyType, DTOType> callback)
    {
        return new GetOrFetchTask<DTOKeyType, DTOType>(callback)
        {
            private Throwable error = null;

            @Override protected DTOType doInBackground(Void... voids)
            {
                DTOType gotOrFetched = null;
                try
                {
                    gotOrFetched = getOrFetch(key, force);
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
                    Listener<DTOKeyType, DTOType> retrievedCallback = getListener();
                    if (retrievedCallback != null)
                    {
                        if (error != null)
                        {
                            retrievedCallback.onErrorThrown(key, error);
                        }
                        else
                        {
                            retrievedCallback.onDTOReceived(key, value);
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
                knownListener.onDTOReceived(key, get(key));
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
