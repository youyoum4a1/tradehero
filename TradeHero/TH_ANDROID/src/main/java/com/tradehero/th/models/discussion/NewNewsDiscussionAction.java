package com.ayondo.academy.models.discussion;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;

public class NewNewsDiscussionAction extends UserDiscussionAction
{
    public NewNewsDiscussionAction(@NonNull AbstractDiscussionCompactDTO discussionDTO)
    {
        super(discussionDTO);
    }
}
