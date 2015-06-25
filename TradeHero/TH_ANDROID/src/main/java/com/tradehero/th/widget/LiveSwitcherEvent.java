package com.tradehero.th.widget;

public class LiveSwitcherEvent
{
    public boolean isFromUser;
    public boolean isLive;

    public LiveSwitcherEvent(boolean isFromUser, boolean isLive)
    {
        this.isFromUser = isFromUser;
        this.isLive = isLive;
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof LiveSwitcherEvent)) return false;

        LiveSwitcherEvent event = (LiveSwitcherEvent) o;

        return isFromUser == event.isFromUser && isLive == event.isLive;
    }
}
