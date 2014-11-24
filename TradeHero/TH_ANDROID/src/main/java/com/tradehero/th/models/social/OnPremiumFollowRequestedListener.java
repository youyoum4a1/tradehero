package com.tradehero.th.models.social;

import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserBaseKey;

public interface OnPremiumFollowRequestedListener
{
    void premiumFollowRequested(@NonNull UserBaseKey heroId);
}
