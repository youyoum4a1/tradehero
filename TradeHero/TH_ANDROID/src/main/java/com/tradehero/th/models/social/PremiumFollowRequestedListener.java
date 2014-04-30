package com.tradehero.th.models.social;

import com.tradehero.th.api.users.UserBaseKey;

public interface PremiumFollowRequestedListener
{
    void premiumFollowRequested(UserBaseKey heroId);
}
