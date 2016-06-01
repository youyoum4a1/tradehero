package com.ayondo.academy.api.discussion.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.discussion.DiscussionKeyList;

public class MessageDiscussionListKeyFactory
{
    @Nullable public static MessageDiscussionListKey next(
            @NonNull MessageDiscussionListKey currentNext,
            @Nullable DiscussionKeyList currentNextValues)
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

    @Nullable public static MessageDiscussionListKey prev(
            @NonNull MessageDiscussionListKey currentPrev,
            @Nullable DiscussionKeyList currentPrevValues)
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
