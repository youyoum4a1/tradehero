package com.androidth.general.api.social;

import android.support.annotation.NonNull;
import com.androidth.general.api.social.key.FollowerHeroRelationId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileCompactDTO;
import java.util.Date;

public class HeroDTO extends UserProfileCompactDTO
{
    public Date followingSince;
    public boolean isFreeFollow;
    public Date stoppedFollowingOn;
    public boolean active;

    @NonNull public FollowerHeroRelationId getHeroId(@NonNull UserBaseKey followerId)
    {
        return new FollowerHeroRelationId(id, followerId.key);
    }
}
