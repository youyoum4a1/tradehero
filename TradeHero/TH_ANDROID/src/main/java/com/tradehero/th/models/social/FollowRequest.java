package com.tradehero.th.models.social;

import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserBaseKey;

public class FollowRequest
{
    @NonNull public final UserBaseKey heroId;
    public final boolean isPremium;

    //<editor-fold desc="Constructors">
    public FollowRequest(@NonNull UserBaseKey heroId, boolean isPremium)
    {
        this.heroId = heroId;
        this.isPremium = isPremium;
    }
    //</editor-fold>
}
