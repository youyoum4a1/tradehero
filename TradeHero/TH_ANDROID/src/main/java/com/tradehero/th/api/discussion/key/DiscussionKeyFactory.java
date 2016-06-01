package com.ayondo.academy.api.discussion.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.ayondo.academy.api.discussion.DiscussionType;
import com.ayondo.academy.api.discussion.MessageHeaderDTO;
import com.ayondo.academy.api.news.key.NewsItemDTOKey;
import com.ayondo.academy.api.timeline.key.TimelineItemDTOKey;

public class DiscussionKeyFactory
{
    @NonNull public static DiscussionKey fromBundle(@NonNull Bundle bundle)
    {
        ensureKeys(bundle);

        DiscussionType discussionType = DiscussionType.valueOf(bundle.getString(DiscussionKey.BUNDLE_KEY_TYPE));

        switch (discussionType)
        {
            case COMMENT:
                return new CommentKey(bundle);
            case TIMELINE_ITEM:
                return new TimelineItemDTOKey(bundle);
            case NEWS:
                return new NewsItemDTOKey(bundle);
            case SECURITY:
                return new SecurityDiscussionKey(bundle);
            case PRIVATE_MESSAGE:
                return new PrivateMessageKey(bundle);
            case BROADCAST_MESSAGE:
                return new BroadcastDiscussionKey(bundle);
        }

        throw new IllegalStateException("Invalid type of DiscussionType " + discussionType);
    }

    @NonNull public static DiscussionKey create(@NonNull DiscussionType discussionType, int id)
    {
        switch (discussionType)
        {
            case COMMENT:
                return new CommentKey(id);
            case TIMELINE_ITEM:
                return new TimelineItemDTOKey(id);
            case NEWS:
                return new NewsItemDTOKey(id);
            case SECURITY:
                return new SecurityDiscussionKey(id);
            case PRIVATE_MESSAGE:
                return new PrivateMessageKey(id);
            case BROADCAST_MESSAGE:
                return new BroadcastDiscussionKey(id);
        }

        throw new IllegalStateException("Invalid type of DiscussionType " + discussionType);
    }

    @NonNull public static DiscussionKey create(@NonNull MessageHeaderDTO messageHeaderDTO)
    {
        return create(messageHeaderDTO.discussionType, messageHeaderDTO.id);
    }

    private static void ensureKeys(@NonNull Bundle bundle)
    {
        if (!bundle.containsKey(DiscussionKey.BUNDLE_KEY_TYPE))
        {
            throw new IllegalStateException("Discussion bundle should contain type of the discussion");
        }
    }
}
