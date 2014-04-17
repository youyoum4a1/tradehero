package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTO;
import java.util.Date;

public class UserRelationDTO implements DTO
{
    public int freeSendsRemaining; // -1 signifies unlimited messages 

    // TODO: client to display these on search-lists
    public boolean isHero;
    public boolean isFollower;
    public boolean isFriend;
    public String friendDesc;

    public Date followerSince;
    public Date heroSince;
}
