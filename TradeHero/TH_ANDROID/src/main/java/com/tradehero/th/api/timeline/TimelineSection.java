package com.tradehero.th.api.timeline;

/**
 * Created by thonguyen on 27/10/14.
 */
public enum TimelineSection
{
    Timeline("timeline"),
    Hot("whatshot");
    private final String name;

    TimelineSection(String name)
    {
        this.name = name;
    }

    @Override public String toString()
    {
        return name;
    }
}