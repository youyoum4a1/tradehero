package com.tradehero.th.api.timeline;

import java.util.List;


public class TimelineReadDTO
{
    public static final String TAG = TimelineReadDTO.class.getSimpleName();

    public List<Integer> readTimelineItemsId;
    public int userUnreadCount;
    public int userAlertCount;
    public boolean markAllAsRead;
    
    public TimelineReadDTO()
    {
        super();
    }
}
