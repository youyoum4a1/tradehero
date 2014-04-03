package com.tradehero.th.api.social;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import java.util.Date;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 7:13 PM To change this template use File | Settings | File Templates. */
public class HeroDTO extends UserProfileCompactDTO
{
    public static final String TAG = HeroDTO.class.getSimpleName();

    public Date followingSince;
    public Date stoppedFollowingOn;
    public boolean active;

    //TODO whether I've paid money to follow this hero
    public boolean paid;

    public HeroId getHeroId(UserBaseKey followerId)
    {
        return new HeroId(id, followerId.key);
    }
}
