package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import retrofit.Callback;
import retrofit.client.Response;

public class MiddleCallbackDiscussion extends BaseMiddleCallback<DiscussionDTO>
{
    private final DiscussionDTOFactory discussionDTOFactory;
    private final UserMessagingRelationshipCache userMessagingRelationshipCache;
    private final DiscussionCache discussionCache;
    private final DiscussionKey stubKey;

    public MiddleCallbackDiscussion(
            Callback<DiscussionDTO> primaryCallback,
            DiscussionDTOFactory discussionDTOFactory,
            UserMessagingRelationshipCache userMessagingRelationshipCache,
            DiscussionCache discussionCache,
            DiscussionKey stubKey)
    {
        super(primaryCallback);
        this.discussionDTOFactory = discussionDTOFactory;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.discussionCache = discussionCache;
        this.stubKey = stubKey;
    }

    @Override public void success(DiscussionDTO discussionDTO, Response response)
    {
        if (stubKey != null)
        {
            discussionCache.invalidate(stubKey);
        }
        if (discussionDTO != null)
        {
            userMessagingRelationshipCache.invalidate(new UserBaseKey(discussionDTO.userId));
            discussionCache.put(discussionDTO.getDiscussionKey(), discussionDTO);
        }
        if (discussionDTOFactory != null)
        {
            discussionDTO = discussionDTOFactory.createChildClass(discussionDTO);
        }
        if (discussionDTO != null)
        {
            discussionDTO.stubKey = stubKey;
        }
        super.success(discussionDTO, response);
    }
}
