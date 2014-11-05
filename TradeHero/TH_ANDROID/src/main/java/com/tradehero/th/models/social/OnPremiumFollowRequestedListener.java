package com.tradehero.th.models.social;

import com.tradehero.th.api.users.UserBaseKey;
import android.support.annotation.NonNull;

public interface OnPremiumFollowRequestedListener
{
    void premiumFollowRequested(@NonNull UserBaseKey heroId);
}
