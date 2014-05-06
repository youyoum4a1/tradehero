package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTO;
import java.util.Date;

public class UserMessagingRelationshipDTO implements DTO
{
    // TODO: client to display these on search-lists
    public boolean isHero;
    public boolean isFollower;
    public boolean isFriend;
    public String friendDesc;

    public Date followerSince;
    public Date heroSince;
    public boolean freeFollow;

    @Override public String toString()
    {
        return "UserMessagingRelationshipDTO{" +
                ", isHero=" + isHero +
                ", isFollower=" + isFollower +
                ", isFriend=" + isFriend +
                ", friendDesc='" + friendDesc + '\'' +
                ", followerSince=" + followerSince +
                ", heroSince=" + heroSince +
                ", freeFollow=" + freeFollow +
                '}';
    }
}
