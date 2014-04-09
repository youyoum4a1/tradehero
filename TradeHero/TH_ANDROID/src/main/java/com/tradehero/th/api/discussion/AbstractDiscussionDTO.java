package com.tradehero.th.api.discussion;

import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
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
    public int upvoteCount;
    public int downvoteCount;
    public int voteDirection; //-1: down, 0: cancel, 1: up
    public int commentCount;
    public String langCode;

    public void populateVote(AbstractDiscussionDTO target)
    {
        target.upvoteCount = upvoteCount;
        target.downvoteCount = downvoteCount;
        target.voteDirection = voteDirection;
    }

    public DiscussionKey getDiscussionKey()
    {
        return new DiscussionKey(id);
    }
}
