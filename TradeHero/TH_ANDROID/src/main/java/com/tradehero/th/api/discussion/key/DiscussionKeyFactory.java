package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import javax.inject.Inject;

public class DiscussionKeyFactory
{
    @Inject public DiscussionKeyFactory()
    {
        super();
    }

    public DiscussionKey fromBundle(Bundle bundle)
    {
        ensureKeys(bundle);

        DiscussionType discussionType = DiscussionType.fromDescription(bundle.getString(DiscussionKey.BUNDLE_KEY_TYPE));

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

    public DiscussionKey create(DiscussionType discussionType, int id)
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

    public DiscussionKey create(MessageHeaderDTO messageHeaderDTO)
    {
        return create(messageHeaderDTO.discussionType, messageHeaderDTO.id);
    }

    private void ensureKeys(Bundle bundle)
    {
        if (!bundle.containsKey(DiscussionKey.BUNDLE_KEY_TYPE))
        {
            throw new IllegalStateException("Discussion bundle should contain type of the discussion");
        }
    }

    public DiscussionListKey toListKey(DiscussionKey discussionKey)
    {
        // for more than one type
        return new DiscussionListKey(discussionKey.getType(), discussionKey.id);
    }
}
