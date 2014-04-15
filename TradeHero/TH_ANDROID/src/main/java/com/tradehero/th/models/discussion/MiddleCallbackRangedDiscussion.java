package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.pagination.RangedDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;

/**
 * Created by xavier2 on 2014/4/9.
 */
public class MiddleCallbackRangedDiscussion extends MiddleCallback<RangedDTO<DiscussionDTO, DiscussionDTOList<DiscussionDTO>>>
{
    public MiddleCallbackRangedDiscussion(Callback<RangedDTO<DiscussionDTO, DiscussionDTOList<DiscussionDTO>>> primaryCallback)
    {
        super(primaryCallback);
    }
}
