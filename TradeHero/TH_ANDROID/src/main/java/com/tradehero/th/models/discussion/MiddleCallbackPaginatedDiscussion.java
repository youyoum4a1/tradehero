package com.tradehero.th.models.discussion;

import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import retrofit.Callback;

/**
 * Created by xavier2 on 2014/4/9.
 */
public class MiddleCallbackPaginatedDiscussion extends BaseMiddleCallback<PaginatedDTO<DiscussionDTO>>
{
    public MiddleCallbackPaginatedDiscussion(Callback<PaginatedDTO<DiscussionDTO>> primaryCallback)
    {
        super(primaryCallback);
    }
}
