package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import retrofit.Callback;
import retrofit.client.Response;

public class MiddleCallbackDiscussion extends MiddleCallback<DiscussionDTO>
{
    private final DiscussionDTOFactory discussionDTOFactory;
    private final UserMessagingRelationshipCache userMessagingRelationshipCache;
    private final DiscussionCache discussionCache;

    public MiddleCallbackDiscussion(
            Callback<DiscussionDTO> primaryCallback,
            DiscussionDTOFactory discussionDTOFactory,
            UserMessagingRelationshipCache userMessagingRelationshipCache,
            DiscussionCache discussionCache)
    {
        super(primaryCallback);
        this.discussionDTOFactory = discussionDTOFactory;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.discussionCache = discussionCache;
    }

    @Override public void success(DiscussionDTO discussionDTO, Response response)
    {
        if (discussionDTO != null)
        {
            userMessagingRelationshipCache.invalidate(new UserBaseKey(discussionDTO.userId));
            discussionCache.put(discussionDTO.getDiscussionKey(), discussionDTO);
        }
        if (discussionDTOFactory != null)
        {
            discussionDTO = discussionDTOFactory.createChildClass(discussionDTO);
        }
        super.success(discussionDTO, response);
    }
}
