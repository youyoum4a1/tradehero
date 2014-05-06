package com.tradehero.common.persistence;

import com.tradehero.common.milestone.BaseAsyncTaskMilestone;
import timber.log.Timber;


abstract public class DTORetrievedAsyncMilestone<
        DTOKeyType extends DTOKey,
        DTOType extends DTO,
        DTOCacheType extends DTOCache<DTOKeyType, DTOType>>
    extends BaseAsyncTaskMilestone<Void, Void, DTOType>
{
    private boolean running = false;
    protected final DTOKeyType key;
    protected Throwable error = null;

    public DTORetrievedAsyncMilestone(DTOKeyType key)
    {
        this.key = key;
        if (key == null)
        {
            throw new NullPointerException("Key cannot be null");
        }
    }

    abstract protected DTOCacheType getCache();

    @Override protected DTOType doInBackground(Void... params)
    {
        DTOType value = null;
        this.error = null;
        try
        {
            value = getCache().getOrFetch(key);
        }
        catch (Throwable throwable)
        {
            this.error = throwable;
        }
        return value;
    }

    public void launchOwn()
    {
        if (isComplete())
        {
            notifyCompleteListener();
        }
        else if (isRunning())
        {
            Timber.d("Task is already running for key %s", key);
        }
        else
        {
            running = true;
            detachTask();
            task = createAsyncTask();
            task.execute();
        }
    }

    @Override protected void onPostExecute(DTOType dtoType)
    {
        detachTask();
        running = false;
        conditionalNotifyFailedListener(error);
        conditionalNotifyCompleteListener();
    }

    @Override public boolean isRunning()
    {
        return running;
    }

    @Override public boolean isComplete()
    {
        return getCache().get(key) != null;
    }

    @Override public boolean isFailed()
    {
        return error != null;
    }
}
