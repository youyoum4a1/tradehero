package com.ayondo.academy.models.discussion;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.security.SecurityId;

public class OpenWatchlistUserAction extends UserDiscussionAction
{
    @NonNull public final SecurityId securityId;

    public OpenWatchlistUserAction(@NonNull AbstractDiscussionCompactDTO discussionDTO,
            @NonNull SecurityId securityId)
    {
        super(discussionDTO);
        this.securityId = securityId;
    }
}
