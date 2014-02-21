package com.tradehero.th.api.local;

import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTOEnhanced;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import java.util.LinkedList;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/10/13 Time: 12:48 PM Copyright (c) TradeHero */
public class TimelineItemBuilder
{
    private List<TimelineItem> items = new LinkedList<>();
    private TimelineDTO source;

    public TimelineItemBuilder(TimelineDTO dto)
    {
        buildFrom(dto);
    }

    public void buildFrom(TimelineDTO dto)
    {
        this.source = dto;
        build();
    }

    private void build()
    {
        buildItems();
    }

    private void buildItems()
    {
        if (source != null)
        {
            for (TimelineItemDTOEnhanced itemDTO: source.enhancedItems)
            {
                TimelineItem item = new TimelineItem(itemDTO);
                item.setUser(userForUserId(itemDTO.userId));
                items.add(item);
            }
        }
    }

    private UserProfileCompactDTO userForUserId(int userId)
    {
        // TODO create a hashmap mapping userId & user object
        if (source != null)
        {
            for (UserProfileCompactDTO user: source.users)
            {
                if (user.id == userId) {
                    return user;
                }
            }
        }
        return null;
    }

    public List<TimelineItem> getItems()
    {
        return items;
    }
}
