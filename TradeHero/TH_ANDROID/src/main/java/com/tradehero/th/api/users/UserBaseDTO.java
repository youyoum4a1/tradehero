package com.tradehero.th.api.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.ExtendedDTO;
import java.io.IOException;
import java.util.Date;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 6:47 PM Copyright (c) TradeHero */
public class UserBaseDTO extends ExtendedDTO
{
    public int id;
    public String picture;
    public String displayName;
    public String firstName;
    public String lastName;
    public Date memberSince;
    public boolean isAdmin;
    public String activeSurveyURL;
    public String activeSurveyImageURL;
    public Double roiSinceInception;
    public String countryCode;

    public UserRelationDTO relationship;

    //TODO fake data,may need to change
    //public boolean isFreeUser;

    public UserBaseDTO()
    {
    }

    @JsonIgnore
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
        try
        {
            return THJsonAdapter.getInstance().toStringBody(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "Failed to json";
        }
    }
}
