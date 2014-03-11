package com.tradehero.th.api.timeline;

import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import java.util.Collections;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/3/13 Time: 12:52 PM Copyright (c) TradeHero */
public class TimelineDTO
{
    private List<UserProfileCompactDTO> users;
    private List<SecurityCompactDTO> securities;
    private List<TimelineItemDTO> items;
    private List<TimelineItemDTOEnhanced> enhancedItems;

    public UserProfileCompactDTO getUserById(int userId)
    {
        return null;
    }

    public List<TimelineItemDTOEnhanced> getEnhancedItems()
    {
        return Collections.unmodifiableList(enhancedItems);
    }

    public void setEnhancedItems(List<TimelineItemDTOEnhanced> enhancedItems)
    {
        this.enhancedItems = enhancedItems;
    }

    public List<TimelineItemDTO> getItems()
    {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<TimelineItemDTO> items)
    {
        this.items = items;
    }

    public List<SecurityCompactDTO> getSecurities()
    {
        return Collections.unmodifiableList(securities);
    }

    public void setSecurities(List<SecurityCompactDTO> securities)
    {
        this.securities = securities;
    }

    public List<UserProfileCompactDTO> getUsers()
    {
        return Collections.unmodifiableList(users);
    }

    public void setUsers(List<UserProfileCompactDTO> users)
    {
        this.users = users;
    }
}