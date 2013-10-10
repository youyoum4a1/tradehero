package com.tradehero.th.api.users;

import java.util.Date;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 6:47 PM Copyright (c) TradeHero */
public class UserBaseDTO
{
    public int id;
    public String picture;
    public String displayName;
    public String firstName;
    public String lastName;
    public Date memberSince;

    public UserBaseDTO()
    {
    }

    public UserBaseKey getBaseKey()
    {
        return new UserBaseKey(id);
    }
}
