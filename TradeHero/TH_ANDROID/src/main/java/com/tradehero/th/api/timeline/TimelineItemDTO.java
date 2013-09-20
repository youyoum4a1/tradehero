package com.tradehero.th.api.timeline;

import com.tradehero.th.api.trade.TradeDTO;
import java.util.Date;

/** Created with IntelliJ IDEA. User: tho Date: 9/3/13 Time: 12:52 PM Copyright (c) TradeHero */
public class TimelineItemDTO
{
    public int      id;
    public Date createdAtUtc;
    public int      userId;
    public String   comment;
    public TradeDTO trade;
    public int      securityId;
}
