package com.tradehero.th.models.discussion;

import android.support.annotation.NonNull;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.security.SecurityId;

public class SecurityUserAction extends UserDiscussionAction
{
    @NonNull public final SecurityId securityId;

    public SecurityUserAction(@NonNull AbstractDiscussionCompactDTO discussionDTO,
            @NonNull SecurityId securityId)
    {
        super(discussionDTO);
        this.securityId = securityId;
    }
}