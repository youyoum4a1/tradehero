package com.tradehero.common.persistence;

import com.tradehero.common.milestone.BaseMilestone;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.utils.THLog;
import java.lang.ref.WeakReference;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 5:40 PM To change this template use File | Settings | File Templates. */
abstract public class DTORetrievedMilestone<
        DTOKeyType extends DTOKey,
        DTOType extends DTO,
        DTOCacheType extends DTOCache<DTOKeyType, DTOType>>
    extends BaseMilestone
{
    public static final String TAG = DTORetrievedMilestone.class.getSimpleName();

    private boolean failed = false;
    protected final DTOKeyType key;
    protected final DTOCache.Listener<DTOKeyType, DTOType> cacheListener;
    protected DTOCache.GetOrFetchTask<DTOType> fetchTask;

    public DTORetrievedMilestone(DTOKeyType key)
    {
        this.key = key;
        if (key == null)
        {
            throw new NullPointerException("Key cannot be null");
        }
        cacheListener = new DTOCache.Listener<DTOKeyType, DTOType>()
        {
            @Override public void onDTOReceived(DTOKeyType key, DTOType value)
            {
                fetchTask = null;
                conditionalNotifyCompleteListener();
            }

            @Override public void onErrorThrown(DTOKeyType key, Throwable error)
            {
                fetchTask = null;
                failed = true;
                notifyFailedListener(error);
            }
        };
    }

    @Override public void onDestroy()
    {
        parentCompleteListener.clear();
        if (fetchTask != null)
        {
            fetchTask.forgetListener(true);
        }
        fetchTask = null;
    }

    abstract protected DTOCacheType getCache();

    public void launchOwn()
    {
        failed = false;

        if (isComplete())
        {
            notifyCompleteListener();
        }
        else if (isRunning())
        {
            THLog.d(TAG, "Task is already running for key " + key);
        }
        else
        {
            fetchTask = getCache().getOrFetch(key, cacheListener);
            fetchTask.execute();
        }
    }

    public boolean isRunning()
    {
        return fetchTask != null;
    }

    @Override public boolean isComplete()
    {
        return getCache().get(key) != null;
    }

    @Override public boolean isFailed()
    {
        return failed;
    }

    protected void notifyFailedListener(Throwable throwable)
    {
        failed = true;
        super.notifyFailedListener(throwable);
    }
}
