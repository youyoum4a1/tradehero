package com.androidth.general.models.discussion;

import android.support.annotation.NonNull;
import com.androidth.general.api.discussion.AbstractDiscussionCompactDTO;
import com.androidth.general.api.users.UserBaseKey;

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
