package com.tradehero.common.milestone;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 5:26 PM To change this template use File | Settings | File Templates. */
public class BaseMilestoneGroup extends BaseMilestone implements MilestoneGroup
{
    public static final String TAG = BaseMilestoneGroup.class.getSimpleName();

    protected final List<Milestone> milestones;
    private final OnCompleteListener childCompleteListener;

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
        launchOwn();
    }

    public void launchOwn()
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
