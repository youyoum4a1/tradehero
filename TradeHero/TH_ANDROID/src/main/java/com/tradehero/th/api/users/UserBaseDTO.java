package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTO;
import java.util.Date;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 6:47 PM Copyright (c) TradeHero */
public class UserBaseDTO  implements DTO
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

    @Override public int hashCode()
    {
        return new Integer(id).hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof UserBaseDTO) && (new Integer(id)).equals(((UserBaseDTO) other).id);
    }

    @Override public String toString()
    {
        return "UserBaseDTO{" +
                "displayName='" + displayName + '\'' +
                ", id=" + id +
                ", picture='" + picture + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", memberSince=" + memberSince +
                '}';
    }
}
