package com.androidth.general.api.timeline;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.users.UserProfileCompactDTO;
import java.util.Collections;
import java.util.List;

public class TimelineDTO implements DTO
{
    @Nullable private List<UserProfileCompactDTO> users;
    private List<SecurityCompactDTO> securities;
    private List<TimelineItemDTO> enhancedItems;

    public UserProfileCompactDTO getUserById(int userId)
    {
        if (users != null)
        {
            for (UserProfileCompactDTO userProfileCompactDTO : users)
            {
                if (userProfileCompactDTO != null && userProfileCompactDTO.id == userId)
                {
                    return userProfileCompactDTO;
                }
            }
        }


        return null;
    }

    @Nullable public List<TimelineItemDTO> getEnhancedItems()
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

    public void setEnhancedItems(List<TimelineItemDTO> enhancedItems)
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

    @NonNull public List<UserProfileCompactDTO> getUsers()
    {
        if (users == null)
        {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(users);
    }

    public void setUsers(List<UserProfileCompactDTO> users)
    {
        this.users = users;
    }
}