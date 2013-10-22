package com.tradehero.th.api.timeline;

import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 10:45 PM To change this template use File | Settings | File Templates. */
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
