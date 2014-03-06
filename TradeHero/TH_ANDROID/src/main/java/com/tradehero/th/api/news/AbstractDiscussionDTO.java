package com.tradehero.th.api.news;

import com.tradehero.common.persistence.DTO;
import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/6/14 Time: 4:41 PM Copyright (c) TradeHero
 */
public class AbstractDiscussionDTO
    implements DTO
{
    public int id;
    public Date createdAtUtc;
    public int userId;
    public String text;
    public int voteCount;
    public int voteDirection; //-1: down, 0: cancel, 1: up
    public int commentCount;
}
