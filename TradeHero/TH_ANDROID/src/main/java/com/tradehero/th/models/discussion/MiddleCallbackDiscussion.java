package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;

/**
 * Created by xavier2 on 2014/4/9.
 */
public class MiddleCallbackDiscussion extends MiddleCallback<DiscussionDTO>
{
    public MiddleCallbackDiscussion(Callback<DiscussionDTO> primaryCallback)
    {
        super(primaryCallback);
    }
}
