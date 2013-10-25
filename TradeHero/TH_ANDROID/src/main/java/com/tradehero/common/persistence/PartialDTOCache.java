package com.tradehero.common.persistence;

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

    private List<WeakReference<Listener<DTOKeyType, DTOType>>> listeners;

    public PartialDTOCache()
    {
        super();
        this.listeners = new ArrayList<>();
    }

    abstract protected DTOType fetch(DTOKeyType key);

    @Override public DTOType getOrFetch(DTOKeyType key)
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

    @Override public GetOrFetchTask<DTOType> getOrFetch(final DTOKeyType key, final Listener<DTOKeyType, DTOType> callback)
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
    @Override public GetOrFetchTask<DTOType> getOrFetch(final DTOKeyType key, final boolean force, final Listener<DTOKeyType, DTOType> callback)
    {
        final WeakReference<Listener<DTOKeyType, DTOType>> weakCallback = new WeakReference<>(callback);

        return new GetOrFetchTask<DTOType>()
        {
            @Override protected DTOType doInBackground(Void... voids)
            {
                return getOrFetch(key, force); // TODO do something about RetrofitError exception
            }

            @Override protected void onPostExecute(DTOType value)
            {
                super.onPostExecute(value);
                // We retrieve the callback right away to avoid having it vanish between the 2 get() calls.
                if (!hasForgottenListener() && !isCancelled())
                {
                    Listener<DTOKeyType, DTOType> retrievedCallback = weakCallback.get();
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
