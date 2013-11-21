package com.tradehero.common.milestone;

/**
 * Groups milestones that all need to be complete for this group to be complete.
 *  Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 5:24 PM To change this template use File | Settings | File Templates.
 *  */
public interface MilestoneGroup extends Milestone
{
    void add(Milestone milestone);
    int getMilestoneCount();
    int getCompleteMilestoneCount();
    int getFailedMilestoneCount();
}
