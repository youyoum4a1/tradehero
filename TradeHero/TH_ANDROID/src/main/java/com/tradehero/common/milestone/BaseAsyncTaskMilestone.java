package com.tradehero.common.milestone;

import android.os.AsyncTask;
import java.lang.ref.WeakReference;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 5:26 PM To change this template use File | Settings | File Templates. */
abstract public class BaseAsyncTaskMilestone<KeyType, ProgressType, ValueType> extends AsyncTask<KeyType, ProgressType, ValueType>
        implements Milestone
{
    public static final String TAG = BaseAsyncTaskMilestone.class.getSimpleName();

    protected WeakReference<OnCompleteListener> parentCompleteListener = new WeakReference<>(null);

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
}
