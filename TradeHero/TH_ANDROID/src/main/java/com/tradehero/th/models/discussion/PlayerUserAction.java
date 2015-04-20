package com.tradehero.th.models.discussion;

import android.support.annotation.NonNull;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.users.UserBaseKey;

public class PlayerUserAction extends UserDiscussionAction
{
    @NonNull public final UserBaseKey userClicked;

    public PlayerUserAction(
            @NonNull AbstractDiscussionCompactDTO discussionDTO,
            @NonNull UserBaseKey userClicked)
    {
        super(discussionDTO);
        this.userClicked = userClicked;
    }
}
