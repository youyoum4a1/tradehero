package com.tradehero.th.api.timeline;

import com.tradehero.th.api.security.SecurityMediaDTO;
import java.util.Date;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/3/13 Time: 12:57 PM Copyright (c) TradeHero */
public class TimelineItemDTOEnhanced
{
    public int id;
    public Date createdAtUtc;
    public Integer userId;
    public String text;
    public int type;
    public Date userViewedAtUtc;
    public List<SecurityMediaDTO> medias;
    public Integer pushTypeId;
}