package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.pagination.RangedDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;

/**
 * Created by xavier2 on 2014/4/9.
 */
public class MiddleCallbackRangedDiscussion extends MiddleCallback<RangedDTO<AbstractDiscussionDTO, DiscussionDTOList>>
{
    public MiddleCallbackRangedDiscussion(Callback<RangedDTO<AbstractDiscussionDTO, DiscussionDTOList>> primaryCallback)
    {
        super(primaryCallback);
    }
}
