package com.tradehero.common.milestone;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 5:26 PM To change this template use File | Settings | File Templates. */
abstract public class BaseMilestone implements Milestone
{
    public static final String TAG = BaseMilestone.class.getSimpleName();

    protected WeakReference<OnCompleteListener> parentCompleteListener = new WeakReference<>(null);

    public BaseMilestone()
    {
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
