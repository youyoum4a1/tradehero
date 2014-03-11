package com.tradehero.th.api.timeline;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.security.SecurityMediaDTO;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/3/13 Time: 12:57 PM Copyright (c) TradeHero */
public class TimelineItemDTOEnhanced implements DTO
{
    public int id;
    public Date createdAtUtc;
    public Integer userId;
    public String text;
    public int type;
    public Date userViewedAtUtc;
    public Integer pushTypeId;

    private List<SecurityMediaDTO> medias;

    public TimelineItemDTOKey getTimelineKey()
    {
        return new TimelineItemDTOKey(id);
    }

    public List<SecurityMediaDTO> getMedias()
    {
        return Collections.unmodifiableList(medias);
    }

    public void setMedias(List<SecurityMediaDTO> medias)
    {
        this.medias = medias;
    }
}