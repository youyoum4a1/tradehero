package com.tradehero.th.models.social;

import com.tradehero.th.api.users.UserBaseKey;
import org.jetbrains.annotations.NotNull;

public interface OnPremiumFollowRequestedListener
{
    void premiumFollowRequested(@NotNull UserBaseKey heroId);
}
