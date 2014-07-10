package com.tradehero.common.persistence;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

abstract public class DTOFetchAssistantNew<DTOKeyType extends DTOKey, DTOType extends DTO>
        extends BasicFetchAssistant<DTOKeyType, DTOType>
        implements DTOCacheNew.Listener<DTOKeyType, DTOType>
{
    public DTOFetchAssistantNew(List<DTOKeyType> keysToFetch)
    {
        super(keysToFetch);
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

    private void fetch(@NotNull DTOKeyType key, boolean force)
    {
        getCache().register(key, this);
        getCache().getOrFetchAsync(key, force);
    }

    @Override public void clear()
    {
        super.clear();
        getCache().unregister(this);
    }

    @NotNull abstract protected DTOCacheNew<DTOKeyType, DTOType> getCache();

    //<editor-fold desc="DTOCache.Listener<DTOKeyType, DTOType>">
    @Override public void onDTOReceived(@NotNull final DTOKeyType key, @NotNull final DTOType value)
    {
        if (fetched.containsKey(key))
        {
            fetched.put(key, value);
            notifyListener();
        }
    }

    @Override public void onErrorThrown(@NotNull final DTOKeyType key, @NotNull final Throwable error)
    {
        Timber.e("Error fetching %s", key, error);
    }
    //</editor-fold>
}
