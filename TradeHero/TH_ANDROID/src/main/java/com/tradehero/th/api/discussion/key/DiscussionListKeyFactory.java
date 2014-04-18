package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.users.CurrentUserId;
import javax.inject.Inject;

public class DiscussionListKeyFactory
{
    private CurrentUserId currentUserId;

    @Inject public DiscussionListKeyFactory(CurrentUserId currentUserId)
    {
        super();
        this.currentUserId = currentUserId;
    }

    public DiscussionListKey create(Bundle args)
    {
        DiscussionListKey discussionListKey = null;
        if (args != null)
        {
            // Perhaps a bit too haphazard?
            if (args.containsKey(DiscussionVoteKey.VOTE_DIRECTION_NAME_BUNDLE_KEY))
            {
                discussionListKey = new DiscussionVoteKey(args);
            }
            else if (args.containsKey(RangedDiscussionListKey.MAX_COUNT_BUNDLE_KEY))
            {
                discussionListKey = new RangedDiscussionListKey(args);
            }
            else if (args.containsKey(PaginatedDiscussionListKey.PAGE_BUNDLE_KEY))
            {
                discussionListKey = new PaginatedDiscussionListKey(args);
            }
            else if (args.containsKey(DiscussionListKey.IN_REPLY_TO_TYPE_NAME_BUNDLE_KEY))
            {
                discussionListKey = new DiscussionListKey(args);
            }
        }
        return discussionListKey;
    }

    public DiscussionListKey create(DiscussionDTO discussionDTO)
    {
        if (discussionDTO == null || discussionDTO.type == null)
        {
            return null;
        }
        if (discussionDTO.type.equals(DiscussionType.PRIVATE_MESSAGE))
        {
            DiscussionType discussionType = discussionDTO.inReplyToType != null ?
                    discussionDTO.inReplyToType :
                    discussionDTO.type;
            int inReplyToId = discussionDTO.inReplyToId != 0 ?
                    discussionDTO.inReplyToId :
                    discussionDTO.id;
            return new PrivateMessageDiscussionListKey(
                    discussionType,
                    inReplyToId,
                    discussionDTO.getSenderKey(),
                    currentUserId.toUserBaseKey(),
                    null, null, null);
        }
        return create(discussionDTO.getDiscussionKey());
    }

    public DiscussionListKey create(DiscussionKey discussionKey)
    {
        return new DiscussionListKey(discussionKey.getType(), discussionKey.id);
    }
}
