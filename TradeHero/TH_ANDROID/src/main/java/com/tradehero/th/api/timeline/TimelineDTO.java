package com.tradehero.th.api.timeline;

import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/3/13 Time: 12:52 PM Copyright (c) TradeHero */
public class TimelineDTO
{
    public List<UserProfileCompactDTO> users;
    public List<SecurityCompactDTO> securities;
    public List<TimelineItemDTO> items;
    public List<TimelineItemDTOEnhanced> enhancedItems;

    public UserProfileCompactDTO getUserById(int userId)
    {
        return null;
    }
}