package com.tradehero.th.api.local;

import com.tradehero.th.api.misc.MediaDTO;
import com.tradehero.th.api.timeline.TimelineItemDTOEnhanced;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.loaders.AbstractItemWithComparableId;
import java.util.Date;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/10/13 Time: 11:31 AM Copyright (c) TradeHero */
public class TimelineItem extends AbstractItemWithComparableId<Integer>
{
    private int timelineItemId;
    private UserProfileCompactDTO user;
    private final Date date;
    private final String text;
    private final List<MediaDTO> medias;
    private final boolean read;
    private boolean selected;

    public TimelineItem(TimelineItemDTOEnhanced dto)
    {
        this.timelineItemId = dto.id;
        this.date = dto.createdAtUtc;
        this.text = dto.text;
        this.medias = dto.medias;
        this.read = dto.userViewedAtUtc != null;
        this.selected = false;
    }

    public MediaDTO firstMediaWithLogo()
    {
        for (MediaDTO m: medias)
        {
            if (m.url != null)
            {
                return m;
            }
        }
        return null;
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

    @Override public Integer getId()
    {
        return timelineItemId;
    }

    @Override public void setId(Integer id)
    {
        timelineItemId = id;
    }
}
