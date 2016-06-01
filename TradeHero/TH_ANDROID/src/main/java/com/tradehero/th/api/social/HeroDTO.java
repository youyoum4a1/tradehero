package com.ayondo.academy.api.social;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.social.key.FollowerHeroRelationId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileCompactDTO;
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
