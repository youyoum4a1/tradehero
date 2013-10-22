package com.tradehero.common.persistence;

import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/4/13 Time: 11:01 AM To change this template use File | Settings | File Templates.
 * The registered listeners are kept as weak references. So they should be strongly referenced elsewhere.
 *  */
abstract public class StraightDTOCache<DTOKeyType extends DTOKey, DTOType>
        implements LiveDTOCache<DTOKeyType, DTOType>
{
    private LruCache<DTOKeyType, DTOType> lruCache;
    private List<WeakReference<Listener<DTOKeyType, DTOType>>> listeners;

    public StraightDTOCache(int maxSize)
    {
        super();
        this.lruCache = new LruCache<>(maxSize);
        this.listeners = new ArrayList<>();
    }
    
    abstract protected DTOType fetch(DTOKeyType key);

    @Override public DTOType get(DTOKeyType key)
    {
        if (key == null)
        {
            return null;
        }
        return this.lruCache.get(key);
    }

    @Override public DTOType put(DTOKeyType key, DTOType value)
    {
        return this.lruCache.put(key, value);
    }

    public DTOType getOrFetch(DTOKeyType key)
    {
        return getOrFetch(key, false);
    }

    @Override public DTOType getOrFetch(DTOKeyType key, boolean force)
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

    public AsyncTask<Void, Void, DTOType> getOrFetch(final DTOKeyType key, final Listener<DTOKeyType, DTOType> callback)
    {
       return getOrFetch(key, false, callback);
    }

    @Override public AsyncTask<Void, Void, DTOType> getOrFetch(final DTOKeyType key, final boolean force, final Listener<DTOKeyType, DTOType> callback)
    {
        final WeakReference<Listener<DTOKeyType, DTOType>> weakCallback = new WeakReference<Listener<DTOKeyType, DTOType>>(callback);

        return new AsyncTask<Void, Void, DTOType>()
        {
            @Override protected DTOType doInBackground(Void... voids)
            {
                return getOrFetch(key, force);
            }

            @Override protected void onPostExecute(DTOType value)
            {
                super.onPostExecute(value);
                Listener<DTOKeyType, DTOType> retrievedCallback = weakCallback.get();
                // We retrieve the callback right away to avoid having it vanish between the 2 get() calls.
                if (!isCancelled())
                {
                    if (retrievedCallback != null)
                    {
                        retrievedCallback.onDTOReceived(key, value);
                    }
                    pushToListeners(key);
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
}
