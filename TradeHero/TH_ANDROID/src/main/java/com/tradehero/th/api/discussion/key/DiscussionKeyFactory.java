package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.news.NewsItemDTOKey;
import com.tradehero.th.api.timeline.TimelineItemDTOKey;

/**
 * Created by thonguyen on 9/4/14.
 */
public class DiscussionKeyFactory
{
    public static DiscussionKey fromBundle(Bundle bundle)
    {
        ensureKeys(bundle);

        int discussionId = bundle.getInt(DiscussionKey.BUNDLE_KEY_ID);
        DiscussionType discussionType = DiscussionType.fromDescription(bundle.getString(DiscussionKey.BUNDLE_KEY_TYPE));

        switch (discussionType)
        {
            case COMMENT:
                return new CommentKey(discussionId);
            case TIMELINE_ITEM:
                return new TimelineItemDTOKey(discussionId);
            case NEWS:
                return new NewsItemDTOKey(discussionId);
            case SECURITY:
                throw new RuntimeException("Not implemented for security type");
            case PRIVATE_MESSAGE:
                throw new RuntimeException("Not implemented for private messaging type");
            case BROADCAST_MESSAGE:
                throw new RuntimeException("Not implemented for broadcast type");
        }

        throw new IllegalStateException("Invalid type of DiscussionType");
    }

    private static void ensureKeys(Bundle bundle)
    {
        if (!bundle.containsKey(DiscussionKey.BUNDLE_KEY_ID))
        {
            throw new IllegalStateException("Discussion bundle should contain id of the discussion");
        }

        if (!bundle.containsKey(DiscussionKey.BUNDLE_KEY_TYPE))
        {
            throw new IllegalStateException("Discussion bundle should contain type of the discussion");
        }
    }
}
