package com.tradehero.common.milestone;

public interface DependentMilestone extends Milestone
{
    Milestone getDependsOn();
    void setDependsOn(Milestone milestone);
    void launchOwn();
}
