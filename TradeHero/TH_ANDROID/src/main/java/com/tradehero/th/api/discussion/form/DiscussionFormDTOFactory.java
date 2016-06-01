package com.ayondo.academy.api.discussion.form;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.discussion.DiscussionType;
import com.ayondo.academy.api.news.form.NewsItemReplyDiscussionFormDTO;
import com.ayondo.academy.api.timeline.form.TimelineItemReplyCommentFormDTO;

public class DiscussionFormDTOFactory
{
    @NonNull public static DiscussionFormDTO createEmpty(@NonNull DiscussionType discussionType)
    {
        DiscussionFormDTO created;
        switch (discussionType)
        {
            case COMMENT:
                created = new ReplyCommentFormDTO();
                break;
            case TIMELINE_ITEM:
                created = new TimelineItemReplyCommentFormDTO();
                break;
            case SECURITY:
                created = new SecurityReplyDiscussionFormDTO();
                break;
            case NEWS:
                created = new NewsItemReplyDiscussionFormDTO();
                break;
            case PRIVATE_MESSAGE:
                created = new PrivateReplyDiscussionFormDTO();
                break;
            case BROADCAST_MESSAGE:
                created = new BroadcastReplyDiscussionFormDTO();
                break;
            default:
                throw new IllegalStateException("Invalid type of DiscussionType" + discussionType);
        }
        return created;
    }
}
