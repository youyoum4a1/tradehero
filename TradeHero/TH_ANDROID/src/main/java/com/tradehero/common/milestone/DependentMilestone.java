package com.tradehero.common.milestone;

/**
 * Milestone that depends on another completing to be able to run
 * Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:04 PM To change this template use File | Settings | File Templates.
 * */
public interface DependentMilestone extends Milestone
{
    Milestone getDependsOn();
    void setDependsOn(Milestone milestone);
    void launchOwn();
}
