package com.tradehero.th.api.social;

import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import java.util.Date;

public class HeroDTO extends UserProfileCompactDTO
{
    public Date followingSince;
    public boolean isFreeFollow;
    public Date stoppedFollowingOn;
    public boolean active;

    public FollowerHeroRelationId getHeroId(UserBaseKey followerId)
    {
        return new FollowerHeroRelationId(id, followerId.key);
    }
}
