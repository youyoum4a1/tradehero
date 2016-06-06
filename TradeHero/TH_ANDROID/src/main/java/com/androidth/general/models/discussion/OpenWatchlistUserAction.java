package com.androidth.general.models.discussion;

import android.support.annotation.NonNull;
import com.androidth.general.api.discussion.AbstractDiscussionCompactDTO;
import com.androidth.general.api.security.SecurityId;

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
