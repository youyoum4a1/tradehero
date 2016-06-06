package com.androidth.general.models.discussion;

import android.support.annotation.NonNull;
import com.androidth.general.api.news.NewsItemCompactDTO;

public class OpenWebUserAction extends UserDiscussionAction
{
    public OpenWebUserAction(@NonNull NewsItemCompactDTO discussionDTO)
    {
        super(discussionDTO);
    }
}
