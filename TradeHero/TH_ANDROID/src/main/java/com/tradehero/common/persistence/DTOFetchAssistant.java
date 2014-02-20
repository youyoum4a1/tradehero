package com.tradehero.common.persistence;

import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/1/13 Time: 3:47 PM To change this template use File | Settings | File Templates. */
abstract public class DTOFetchAssistant<DTOKeyType extends DTOKey, DTOType extends DTO>
        extends BasicFetchAssistant<DTOKeyType, DTOType>
        implements DTOCache.Listener<DTOKeyType, DTOType>
{
    public static final String TAG = DTOFetchAssistant.class.getSimpleName();

    private final Map<DTOKeyType, DTOCache.GetOrFetchTask<DTOKeyType, DTOType>> fetchTasks;

    public DTOFetchAssistant(List<DTOKeyType> keysToFetch)
    {
        super(keysToFetch);
        fetchTasks = new HashMap<>();
        if (keysToFetch != null)
        {
            for (DTOKeyType key: keysToFetch)
            {
                if (key != null)
                {
                    fetchTasks.put(key, null);
                }
            }
        }
    }

    public void execute(boolean force)
    {
        boolean ready = true;
        for (DTOKeyType key: new ArrayList<>(fetched.keySet())) // Make a new list to avoid changes
        {
            fetched.put(key, getCache().get(key)); // Supposedly makes it faster
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
        DTOCache.GetOrFetchTask<DTOKeyType, DTOType> fetchTask = fetchTasks.get(key);
        if (fetchTask != null)
        {
            fetchTask.setListener(null);
        }
        fetchTask = getCache().getOrFetch(key, force, this);
        fetchTasks.put(key, fetchTask);
        fetchTask.execute();
    }

    @Override public void clear()
    {
        super.clear();
        for (DTOCache.GetOrFetchTask<DTOKeyType, DTOType> task: fetchTasks.values())
        {
            if (task != null)
            {
                task.setListener(null);
            }
        }
        fetchTasks.clear();
    }

    abstract protected DTOCache<DTOKeyType, DTOType> getCache();

    //<editor-fold desc="DTOCache.Listener<DTOKeyType, DTOType>">
    @Override public void onDTOReceived(final DTOKeyType key, final DTOType value, boolean fromCache)
    {
        if (key != null && fetched.containsKey(key))
        {
            fetched.put(key, value);
            notifyListener();
        }
    }

    @Override public void onErrorThrown(final DTOKeyType key, final Throwable error)
    {
        Timber.e("Error fetching %s", key, error);
    }
    //</editor-fold>

    public boolean hasActiveFetchTasks()
    {
        for (DTOCache.GetOrFetchTask<DTOKeyType, DTOType> task: fetchTasks.values())
        {
            if (task != null && task.getStatus() != AsyncTask.Status.FINISHED)
            {
                return true;
            }
        }
        return false;
    }
}
