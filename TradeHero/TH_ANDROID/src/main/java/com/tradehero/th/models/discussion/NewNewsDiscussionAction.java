package com.tradehero.th.models.discussion;

import android.support.annotation.NonNull;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;

public class NewNewsDiscussionAction extends UserDiscussionAction
{
    public NewNewsDiscussionAction(@NonNull AbstractDiscussionCompactDTO discussionDTO)
    {
        super(discussionDTO);
    }
}
