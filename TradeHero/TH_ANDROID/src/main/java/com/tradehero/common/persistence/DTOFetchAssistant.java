package com.tradehero.common.persistence;

import android.os.AsyncTask;
import com.tradehero.common.utils.THLog;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/1/13 Time: 3:47 PM To change this template use File | Settings | File Templates. */
abstract public class DTOFetchAssistant<DTOKeyType extends DTOKey, DTOType extends DTO> implements DTOCache.Listener<DTOKeyType, DTOType>
{
    public static final String TAG = DTOFetchAssistant.class.getSimpleName();

    protected WeakReference<OnInfoFetchedListener<DTOKeyType, DTOType>> weakListener = new WeakReference<>(null);

    private final Map<DTOKeyType, DTOType> fetched;
    private final Map<DTOKeyType, DTOCache.GetOrFetchTask<DTOType>> fetchTasks;

    public DTOFetchAssistant(List<DTOKeyType> keysToFetch)
    {
        super();
        fetched = new HashMap<>();
        fetchTasks = new HashMap<>();
        if (keysToFetch != null)
        {
            for (DTOKeyType key: keysToFetch)
            {
                if (key != null)
                {
                    fetched.put(key, null);
                    fetchTasks.put(key, null);
                }
            }
        }
    }

    public void execute()
    {
        execute(false);
    }

    public void execute(boolean force)
    {
        boolean ready = true;
        for (DTOKeyType key: fetched.keySet())
        {
            if (force || fetched.get(key) == null)
            {
                ready = false;
                fetch(key, force);
            }
        }
        if (ready)
        {
            notifyListener();
        }
    }

    private void fetch(DTOKeyType key, boolean force)
    {
        DTOCache.GetOrFetchTask<DTOType> fetchTask = fetchTasks.get(key);
        if (fetchTask != null)
        {
            fetchTask.forgetListener(true);
        }
        fetchTask = getCache().getOrFetch(key, force, this);
        fetchTasks.put(key, fetchTask);
        fetchTask.execute();
    }

    public void clear()
    {
        for (DTOCache.GetOrFetchTask<DTOType> task: fetchTasks.values())
        {
            if (task != null)
            {
                task.forgetListener(true);
            }
        }
        fetchTasks.clear();
        fetched.clear();
    }

    //<editor-fold desc="DTOCache.Listener<DTOKeyType, DTOType>">
    @Override public void onDTOReceived(final DTOKeyType key, final DTOType value)
    {
         if (key != null && fetched.containsKey(key))
         {
             fetched.put(key, value);
             notifyListener();
         }
    }

    @Override public void onErrorThrown(final DTOKeyType key, final Throwable error)
    {
        THLog.e(TAG, "Error fetching " + key, error);
    }
    //</editor-fold>

    abstract protected DTOCache<DTOKeyType, DTOType> getCache();

    /**
     * The listener should be strongly referenced elsewhere
     * @param listener
     */
    public void setListener(OnInfoFetchedListener<DTOKeyType, DTOType> listener)
    {
        this.weakListener = new WeakReference<>(listener);
    }

    private void notifyListener()
    {
        OnInfoFetchedListener<DTOKeyType, DTOType> listener = weakListener.get();
        if (listener != null)
        {
            listener.onInfoFetched(fetched, isDataComplete());
        }
    }

    public boolean isDataComplete()
    {
        for (DTOType value: fetched.values())
        {
            if (value == null)
            {
                return false;
            }
        }
        return true;
    }

    public boolean hasActiveFetchTasks()
    {
        for (DTOCache.GetOrFetchTask<DTOType> task: fetchTasks.values())
        {
            if (task != null && task.getStatus() != AsyncTask.Status.FINISHED)
            {
                return true;
            }
        }
        return false;
    }

    public static interface OnInfoFetchedListener<DTOKeyType, DTOType>
    {
        void onInfoFetched(Map<DTOKeyType, DTOType> fetched, boolean isDataComplete);
    }
}
