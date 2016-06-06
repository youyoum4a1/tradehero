package com.androidth.general.models.discussion;

import android.support.annotation.NonNull;
import com.androidth.general.api.discussion.AbstractDiscussionCompactDTO;

public class NewNewsDiscussionAction extends UserDiscussionAction
{
    public NewNewsDiscussionAction(@NonNull AbstractDiscussionCompactDTO discussionDTO)
    {
        super(discussionDTO);
    }
}
