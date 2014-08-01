package com.tradehero.th.models.social;

import com.tradehero.th.api.users.UserBaseKey;
import org.jetbrains.annotations.NotNull;

public interface OnFreeFollowRequestedListener
{
    void freeFollowRequested(@NotNull UserBaseKey heroId);
}
