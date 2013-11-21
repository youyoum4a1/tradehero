package com.tradehero.common.milestone;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 5:26 PM To change this template use File | Settings | File Templates. */
public class BaseMilestoneGroup implements MilestoneGroup
{
    public static final String TAG = BaseMilestoneGroup.class.getSimpleName();

    protected final List<Milestone> milestones;
    private final OnCompleteListener childCompleteListener;
    protected WeakReference<OnCompleteListener> parentCompleteListener = new WeakReference<>(null);

    public BaseMilestoneGroup()
    {
        milestones = new ArrayList<>();
        childCompleteListener = new OnCompleteListener()
        {
            @Override public void onComplete(Milestone milestone)
            {
                conditionalNotifyCompleteListener();
            }

            @Override public void onFailed(Milestone milestone, Throwable throwable)
            {
                conditionalNotifyFailedListener(throwable);
            }
        };
    }

    @Override public void onDestroy()
    {
        for (Milestone milestone : milestones)
        {
            if (milestone != null)
            {
                milestone.setOnCompleteListener(null);
                milestone.onDestroy();
            }
        }
        milestones.clear();
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

    @Override public void add(Milestone milestone)
    {
        if (milestone != null)
        {
            milestone.setOnCompleteListener(childCompleteListener);
            milestones.add(milestone);
        }
    }

    @Override public void launch()
    {
        for (Milestone milestone : milestones)
        {
            milestone.launch();
        }
    }

    /**
     * It is running if at least 1 child is running.
     * @return
     */
    @Override public boolean isRunning()
    {
        for (Milestone milestone : milestones)
        {
            if (milestone.isRunning())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * It is complete when all children are complete.
     * @return
     */
    @Override public boolean isComplete()
    {
        for (Milestone milestone : milestones)
        {
            if (!milestone.isComplete())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * It is failed if at least 1 child is failed.
     * @return
     */
    @Override public boolean isFailed()
    {
        for (Milestone milestone : milestones)
        {
            if (milestone.isFailed())
            {
                return true;
            }
        }
        return false;
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

    @Override public int getMilestoneCount()
    {
        return milestones.size();
    }

    @Override public int getCompleteMilestoneCount()
    {
        int total = 0;
        for (Milestone milestone : milestones)
        {
            if (milestone.isComplete())
            {
                total++;
            }
        }
        return total;
    }

    @Override public int getFailedMilestoneCount()
    {
        int total = 0;
        for (Milestone milestone : milestones)
        {
            if (milestone.isFailed())
            {
                total++;
            }
        }
        return total;
    }
}
