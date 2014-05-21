package com.tradehero.common.milestone;

public class BaseDependentMilestoneGroup extends BaseMilestoneGroup
    implements DependentMilestone
{
    protected Milestone dependsOn;
    private final OnCompleteListener dependCompleteListener;

    public BaseDependentMilestoneGroup()
    {
        super();
        dependCompleteListener = new OnCompleteListener()
        {
            @Override public void onComplete(Milestone milestone)
            {
                launchOwn();
                // When its children are complete, then the listener will be notified
            }

            @Override public void onFailed(Milestone milestone, Throwable throwable)
            {
                notifyFailedListener(throwable);
            }
        };
    }

    @Override public void onDestroy()
    {
        dependsOn = null;
        super.onDestroy();
    }

    @Override public Milestone getDependsOn()
    {
        return dependsOn;
    }

    @Override public void setDependsOn(Milestone milestone)
    {
        if (milestone != null)
        {
            milestone.setOnCompleteListener(dependCompleteListener);
        }
        this.dependsOn = milestone;
    }

    @Override public void launch()
    {
        dependsOn.launch();
    }
}
