package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.news.form.NewsItemDiscussionFormDTO;
import com.tradehero.th.api.timeline.form.TimelineItemCommentFormDTO;
import javax.inject.Inject;

/**
 * Created by xavier2 on 2014/4/11.
 */
public class DiscussionFormDTOFactory
{
    @Inject public DiscussionFormDTOFactory()
    {
        super();
    }

    public DiscussionFormDTO createEmpty(DiscussionType discussionType)
    {
        switch (discussionType)
        {
            case COMMENT:
                return new CommentFormDTO();
            case TIMELINE_ITEM:
                return new TimelineItemCommentFormDTO();
            case SECURITY:
                return new SecurityDiscussionFormDTO();
            case NEWS:
                return new NewsItemDiscussionFormDTO();
            case PRIVATE_MESSAGE:
                return new PrivateDiscussionFormDTO();
            case BROADCAST_MESSAGE:
                return new BroadcastDiscussionFormDTO();
        }
        throw new IllegalStateException("Invalid type of DiscussionType" + discussionType);
    }
}
