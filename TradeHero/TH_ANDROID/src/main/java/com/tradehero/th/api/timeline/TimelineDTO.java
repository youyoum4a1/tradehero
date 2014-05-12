package com.tradehero.th.api.timeline;

import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import java.util.Collections;
import java.util.List;

public class TimelineDTO
{
    private List<UserProfileCompactDTO> users;
    private List<SecurityCompactDTO> securities;
    //private List<TimelineItemDTO> items;
    private List<TimelineItemDTOEnhanced> enhancedItems;

    public UserProfileCompactDTO getUserById(int userId)
    {
        for (UserProfileCompactDTO userProfileCompactDTO: users)
        {
            if (userProfileCompactDTO != null && userProfileCompactDTO.id == userId)
            {
                return userProfileCompactDTO;
            }
        }

        return null;
    }

    public List<TimelineItemDTOEnhanced> getEnhancedItems()
    {
        if (enhancedItems != null)
        {
            return Collections.unmodifiableList(enhancedItems);
        }
        else
        {
            return null;
        }
    }

    public void setEnhancedItems(List<TimelineItemDTOEnhanced> enhancedItems)
    {
        this.enhancedItems = enhancedItems;
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