package com.ayondo.academy.models.discussion;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.users.UserBaseKey;

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
