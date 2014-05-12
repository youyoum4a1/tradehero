package com.tradehero.th.api.discussion.key;

import com.tradehero.th.api.discussion.DiscussionKeyList;
import javax.inject.Inject;

public class MessageDiscussionListKeyFactory
{
    @Inject public MessageDiscussionListKeyFactory()
    {
        super();
    }

    public MessageDiscussionListKey next(
            MessageDiscussionListKey currentNext,
            DiscussionKeyList currentNextValues)
    {
        MessageDiscussionListKey next = null;
        if (currentNextValues != null && !currentNextValues.isEmpty())
        {
            DiscussionKey highestKey = currentNextValues.getHighestId();
            if (highestKey != null)
            {
                next = new MessageDiscussionListKey(
                        currentNext.inReplyToType,
                        currentNext.inReplyToId,
                        currentNext.senderUser,
                        currentNext.recipientUser,
                        currentNext.maxCount,
                        null,
                        highestKey.id + 1);
            }
        }
        return next;
    }

    public MessageDiscussionListKey prev(
            MessageDiscussionListKey currentPrev,
            DiscussionKeyList currentPrevValues)
    {
        MessageDiscussionListKey prev = null;
        if (currentPrevValues != null && !currentPrevValues.isEmpty())
        {
            DiscussionKey lowestKey = currentPrevValues.getLowestId();
            if (lowestKey != null)
            {
                prev = new MessageDiscussionListKey(
                        currentPrev.inReplyToType,
                        currentPrev.inReplyToId,
                        currentPrev.senderUser,
                        currentPrev.recipientUser,
                        currentPrev.maxCount,
                        lowestKey.id - 1,
                        null);
            }
        }
        return prev;
    }
}
