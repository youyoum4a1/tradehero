package com.tradehero.th.models;

import com.tradehero.th.api.users.UserProfileDTO;

public class ProfileContent
{

    private String suggestLiReauth;

    public String getSuggestLiReauth()
    {
        return suggestLiReauth;
    }

    public void setSuggestLiReauth(String suggestLiReauth)
    {
        this.suggestLiReauth = suggestLiReauth;
    }

    public String getSuggestFbReauth()
    {
        return suggestFbReauth;
    }

    public void setSuggestFbReauth(String suggestFbReauth)
    {
        this.suggestFbReauth = suggestFbReauth;
    }

    public String getSuggestUpgrade()
    {
        return suggestUpgrade;
    }

    public void setSuggestUpgrade(String suggestUpgrade)
    {
        this.suggestUpgrade = suggestUpgrade;
    }

    public String getSuggestTwReauth()
    {
        return suggestTwReauth;
    }

    public void setSuggestTwReauth(String suggestTwReauth)
    {
        this.suggestTwReauth = suggestTwReauth;
    }

    public UserProfileDTO getProfileDTO()
    {
        return profileDTO;
    }

    public void setProfileDTO(UserProfileDTO profileDTO)
    {
        this.profileDTO = profileDTO;
    }

    private String suggestFbReauth;
    private String suggestUpgrade;
    private String suggestTwReauth;
    private UserProfileDTO profileDTO;//class
}
