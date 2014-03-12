package com.tradehero.th.api.discussion;

import com.tradehero.th.api.ExtendedDTO;
import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/6/14 Time: 4:41 PM Copyright (c) TradeHero
 */
public class AbstractDiscussionDTO extends ExtendedDTO
{
    public int id;
    public Date createdAtUtc;
    public int userId;
    public String text;
    public int voteCount;
    public int voteDirection; //-1: down, 0: cancel, 1: up
    public int upvoteCount;
    public int downvoteCount;
    public int commentCount;
}
