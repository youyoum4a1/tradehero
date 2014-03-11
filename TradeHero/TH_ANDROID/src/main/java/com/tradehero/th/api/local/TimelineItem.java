package com.tradehero.th.api.local;

import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.timeline.TimelineItemDTOEnhanced;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/10/13 Time: 11:31 AM Copyright (c) TradeHero */
public class TimelineItem
{
    private int timelineItemId;
    private UserProfileCompactDTO user;
    private final Date date;
    private final String text;
    private final List<SecurityMediaDTO> medias;
    private final boolean read;
    private boolean selected;

    public TimelineItem(TimelineItemDTOEnhanced dto)
    {
        this.timelineItemId = dto.id;
        this.date = dto.createdAtUtc;
        this.text = dto.text;
        this.medias = dto.getMedias();
        this.read = dto.userViewedAtUtc != null;
        this.selected = false;
    }

    public SecurityMediaDTO getFlavorSecurityForDisplay()
    {
        SecurityMediaDTO securityMediaDTO = null;
        for (SecurityMediaDTO m: medias)
        {
            if (m.securityId != 0)
            {
                securityMediaDTO = m;
            }

            // we prefer the first security with photo
            if (securityMediaDTO !=null && securityMediaDTO.url != null)
            {
                return securityMediaDTO;
            }
        }
        return securityMediaDTO;
    }

    public UserProfileCompactDTO getUser()
    {
        return user;
    }

    void setUser(UserProfileCompactDTO user)
    {
        this.user = user;
    }

    public String getText()
    {
        return text;
    }

    public Date getDate()
    {
        return date;
    }

    public List<SecurityMediaDTO> getMedias()
    {
        return Collections.unmodifiableList(medias);
    }

    public int getTimelineItemId()
    {
        return timelineItemId;
    }
}
