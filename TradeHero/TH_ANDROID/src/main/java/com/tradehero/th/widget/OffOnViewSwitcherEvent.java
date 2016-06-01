package com.ayondo.academy.widget;

public class OffOnViewSwitcherEvent
{
    public final boolean isFromUser;
    public final boolean isOn;

    public OffOnViewSwitcherEvent(boolean isFromUser, boolean isOn)
    {
        this.isFromUser = isFromUser;
        this.isOn = isOn;
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof OffOnViewSwitcherEvent)) return false;

        OffOnViewSwitcherEvent event = (OffOnViewSwitcherEvent) o;

        return isFromUser == event.isFromUser && isOn == event.isOn;
    }
}
