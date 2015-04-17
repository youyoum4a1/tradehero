package com.tradehero.th.models.discussion;

import android.support.annotation.NonNull;
import com.tradehero.th.api.news.NewsItemCompactDTO;

public class OpenWebUserAction extends UserDiscussionAction
{
    public OpenWebUserAction(@NonNull NewsItemCompactDTO discussionDTO)
    {
        super(discussionDTO);
    }
}
