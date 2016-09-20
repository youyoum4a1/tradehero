package com.androidth.general.widget;

public class OffOnViewSwitcherEvent
{
    public final boolean isFromUser;
    public final boolean isOn;

    public final boolean isClickedFromTrending;

    public OffOnViewSwitcherEvent(boolean isFromUser,
                                  boolean isOn)
    {
        this.isFromUser = isFromUser;
        this.isOn = isOn;
        this.isClickedFromTrending = false;
    }

    public OffOnViewSwitcherEvent(boolean isFromUser,
                                  boolean isOn,
                                  boolean isClickedFromTrending)
    {
        this.isFromUser = isFromUser;
        this.isOn = isOn;
        this.isClickedFromTrending = isClickedFromTrending;
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof OffOnViewSwitcherEvent)) return false;

        OffOnViewSwitcherEvent event = (OffOnViewSwitcherEvent) o;

        return isFromUser == event.isFromUser && isOn == event.isOn && event.isClickedFromTrending == isClickedFromTrending;
    }
}
