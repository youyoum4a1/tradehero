package com.ayondo.academy.models.discussion;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.news.NewsItemCompactDTO;

public class OpenWebUserAction extends UserDiscussionAction
{
    public OpenWebUserAction(@NonNull NewsItemCompactDTO discussionDTO)
    {
        super(discussionDTO);
    }
}
