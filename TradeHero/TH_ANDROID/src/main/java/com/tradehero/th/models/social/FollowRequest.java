package com.ayondo.academy.models.social;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.users.UserBaseKey;

public class FollowRequest
{
    @NonNull public final UserBaseKey heroId;

    //<editor-fold desc="Constructors">
    public FollowRequest(@NonNull UserBaseKey heroId)
    {
        this.heroId = heroId;
    }
    //</editor-fold>
}
