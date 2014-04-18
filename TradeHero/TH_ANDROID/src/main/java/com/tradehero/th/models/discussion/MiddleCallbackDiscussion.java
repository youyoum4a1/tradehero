package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import retrofit.Callback;
import retrofit.client.Response;

public class MiddleCallbackDiscussion extends MiddleCallback<DiscussionDTO>
{
    private DiscussionDTOFactory discussionDTOFactory;
    private UserMessagingRelationshipCache userMessagingRelationshipCache;

    public MiddleCallbackDiscussion(
            Callback<DiscussionDTO> primaryCallback,
            DiscussionDTOFactory discussionDTOFactory,
            UserMessagingRelationshipCache userMessagingRelationshipCache)
    {
        super(primaryCallback);
        this.discussionDTOFactory = discussionDTOFactory;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
    }

    @Override public void success(DiscussionDTO discussionDTO, Response response)
    {
        if (discussionDTO != null)
        {
            userMessagingRelationshipCache.invalidate(new UserBaseKey(discussionDTO.userId));
        }
        if (discussionDTOFactory != null)
        {
            discussionDTO = discussionDTOFactory.createChildClass(discussionDTO);
        }
        super.success(discussionDTO, response);
    }
}
