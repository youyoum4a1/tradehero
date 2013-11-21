package com.tradehero.common.milestone;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:06 PM To change this template use File | Settings | File Templates. */
public class BaseDependentMilestoneGroup extends BaseMilestoneGroup
    implements DependentMilestone
{
    public static final String TAG = BaseDependentMilestoneGroup.class.getSimpleName();

    protected Milestone dependsOn;
    private final OnCompleteListener dependCompleteListener;

    public BaseDependentMilestoneGroup()
    {
        super();
        dependCompleteListener = new OnCompleteListener()
        {
            @Override public void onComplete(Milestone milestone)
            {
                launch();
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
}
