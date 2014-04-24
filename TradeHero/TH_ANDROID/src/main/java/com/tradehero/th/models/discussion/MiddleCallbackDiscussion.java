package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.discussion.MessageStatusCache;
import retrofit.Callback;
import retrofit.client.Response;

/**
 * Created by xavier2 on 2014/4/9.
 */
public class MiddleCallbackDiscussion extends MiddleCallback<DiscussionDTO>
{
    private DiscussionDTOFactory discussionDTOFactory;
    private MessageStatusCache messageStatusCache;

    public MiddleCallbackDiscussion(
            Callback<DiscussionDTO> primaryCallback,
            DiscussionDTOFactory discussionDTOFactory,
            MessageStatusCache messageStatusCache)
    {
        super(primaryCallback);
        this.discussionDTOFactory = discussionDTOFactory;
        this.messageStatusCache = messageStatusCache;
    }

    @Override public void success(DiscussionDTO discussionDTO, Response response)
    {
        if (discussionDTO != null)
        {
            messageStatusCache.invalidate(new UserBaseKey(discussionDTO.userId));
        }
        if (discussionDTOFactory != null)
        {
            discussionDTO = discussionDTOFactory.createChildClass(discussionDTO);
        }
        super.success(discussionDTO, response);
    }
}
