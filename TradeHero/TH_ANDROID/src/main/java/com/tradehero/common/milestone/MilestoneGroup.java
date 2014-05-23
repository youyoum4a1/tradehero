package com.tradehero.common.milestone;

public interface MilestoneGroup extends Milestone
{
    void add(Milestone milestone);
    int getMilestoneCount();
    int getCompleteMilestoneCount();
    int getFailedMilestoneCount();
}
