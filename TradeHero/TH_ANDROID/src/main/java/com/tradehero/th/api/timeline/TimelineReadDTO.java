package com.tradehero.th.api.timeline;

import java.util.List;

public class TimelineReadDTO
{
    public List<Integer> readTimelineItemsId;
    public int userUnreadCount;
    public int userAlertCount;
    public boolean markAllAsRead;
    
    public TimelineReadDTO()
    {
        super();
    }
}
