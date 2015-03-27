package com.tradehero.th.models.discussion;

import android.support.annotation.NonNull;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;

public class UserDiscussionAction
{
    @NonNull public final AbstractDiscussionCompactDTO discussionDTO;

    //<editor-fold desc="Constructors">
    public UserDiscussionAction(@NonNull AbstractDiscussionCompactDTO discussionDTO)
    {
        this.discussionDTO = discussionDTO;
    }
    //</editor-fold>
}
