package com.tradehero.common.milestone;

import android.os.AsyncTask;
import java.lang.ref.WeakReference;


abstract public class BaseAsyncTaskMilestone<KeyType, ProgressType, ValueType>
        implements Milestone
{
    public static final String TAG = BaseAsyncTaskMilestone.class.getSimpleName();

    protected WeakReference<OnCompleteListener> parentCompleteListener = new WeakReference<>(null);
    protected AsyncTask<KeyType, ProgressType, ValueType> task;

    public BaseAsyncTaskMilestone()
    {
        super();
    }

    @Override public void onDestroy()
    {
        parentCompleteListener.clear();
    }

    @Override public OnCompleteListener getOnCompleteListener()
    {
        return parentCompleteListener.get();
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param listener
     */
    @Override public void setOnCompleteListener(OnCompleteListener listener)
    {
        this.parentCompleteListener = new WeakReference<>(listener);
    }

    protected void conditionalNotifyCompleteListener()
    {
        if (isComplete())
        {
            notifyCompleteListener();
        }
    }

    protected void notifyCompleteListener()
    {
        OnCompleteListener listener = getOnCompleteListener();
        if (listener != null)
        {
            listener.onComplete(this);
        }
    }

    protected void conditionalNotifyFailedListener(Throwable throwable)
    {
        if (isFailed())
        {
            notifyFailedListener(throwable);
        }
    }

    protected void notifyFailedListener(Throwable throwable)
    {
        OnCompleteListener listener = getOnCompleteListener();
        if (listener != null)
        {
            listener.onFailed(this, throwable);
        }
    }

    abstract protected ValueType doInBackground(Void... params);
    abstract protected void onPostExecute(ValueType dtoType);

    protected AsyncTask<KeyType, ProgressType, ValueType> createAsyncTask()
    {
        return new BaseAsyncTask();
    }

    protected void detachTask()
    {
        if (task != null)
        {
            task.cancel(false);
        }
        task = null;
    }

    public class BaseAsyncTask extends AsyncTask<KeyType, ProgressType, ValueType>
    {
        @Override protected ValueType doInBackground(KeyType... keyTypes)
        {
            return BaseAsyncTaskMilestone.this.doInBackground();
        }

        @Override protected void onPostExecute(ValueType valueType)
        {
            super.onPostExecute(valueType);
            BaseAsyncTaskMilestone.this.onPostExecute(valueType);
        }
    }
}
