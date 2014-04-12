package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;
import retrofit.client.Response;

/**
 * Created by xavier2 on 2014/4/9.
 */
public class MiddleCallbackDiscussion extends MiddleCallback<DiscussionDTO>
{
    private DiscussionDTOFactory discussionDTOFactory;

    public MiddleCallbackDiscussion(Callback<DiscussionDTO> primaryCallback, DiscussionDTOFactory discussionDTOFactory)
    {
        super(primaryCallback);
        this.discussionDTOFactory = discussionDTOFactory;
    }

    @Override public void success(DiscussionDTO discussionDTO, Response response)
    {
        if (discussionDTOFactory != null)
        {
            discussionDTO = discussionDTOFactory.createChildClass(discussionDTO);
        }
        super.success(discussionDTO, response);
    }
}
