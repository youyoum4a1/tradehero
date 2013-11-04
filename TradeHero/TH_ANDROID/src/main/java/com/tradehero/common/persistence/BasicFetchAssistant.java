package com.tradehero.common.persistence;

import com.tradehero.common.utils.THLog;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/1/13 Time: 4:33 PM To change this template use File | Settings | File Templates. */
abstract public class BasicFetchAssistant<DTOKeyType, DTOType> implements FetchAssistant<DTOKeyType, DTOType>
{
    public static final String TAG = BasicFetchAssistant.class.getSimpleName();

    protected WeakReference<OnInfoFetchedListener<DTOKeyType, DTOType>> weakListener = new WeakReference<>(null);

    protected final Map<DTOKeyType, DTOType> fetched;

    public BasicFetchAssistant(List<DTOKeyType> keysToFetch)
    {
        super();
        fetched = new HashMap<>();
        if (keysToFetch != null)
        {
            for (DTOKeyType key: keysToFetch)
            {
                if (key != null)
                {
                    fetched.put(key, null);
                }
            }
        }
    }

    public void execute()
    {
        execute(false);
    }

    public void clear()
    {
        fetched.clear();
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param listener
     */
    public void setListener(FetchAssistant.OnInfoFetchedListener<DTOKeyType, DTOType> listener)
    {
        this.weakListener = new WeakReference<>(listener);
    }

    protected void notifyListener()
    {
        FetchAssistant.OnInfoFetchedListener<DTOKeyType, DTOType> listener = weakListener.get();
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
}
