package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.news.form.NewsItemDiscussionFormDTO;
import com.tradehero.th.api.timeline.form.TimelineItemCommentFormDTO;
import javax.inject.Inject;

public class DiscussionFormDTOFactory
{
    @Inject public DiscussionFormDTOFactory()
    {
        super();
    }

    public DiscussionFormDTO createEmpty(DiscussionType discussionType)
    {
        DiscussionFormDTO created;
        switch (discussionType)
        {
            case COMMENT:
                created = new CommentFormDTO();
                break;
            case TIMELINE_ITEM:
                created = new TimelineItemCommentFormDTO();
                break;
            case SECURITY:
                created = new SecurityDiscussionFormDTO();
                break;
            case NEWS:
                created = new NewsItemDiscussionFormDTO();
                break;
            case PRIVATE_MESSAGE:
                created = new PrivateDiscussionFormDTO();
                break;
            case BROADCAST_MESSAGE:
                created = new BroadcastDiscussionFormDTO();
                break;
            default:
                throw new IllegalStateException("Invalid type of DiscussionType" + discussionType);
        }
        return created;
    }
}
