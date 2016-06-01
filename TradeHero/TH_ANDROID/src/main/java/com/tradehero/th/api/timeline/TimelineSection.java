package com.ayondo.academy.api.timeline;

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