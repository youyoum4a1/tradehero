package com.tradehero.th.api.users;

/** Created with IntelliJ IDEA. User: tho Date: 9/4/13 Time: 11:27 AM Copyright (c) TradeHero */
public class UserLoginDTO
{
    public UserProfileDTO profileDTO;
    public boolean suggestUpgrade = false; // if true, client will suggest user upgrades client

    public boolean suggestLiReauth = false;
    public boolean suggestTwReauth = false;
    public boolean suggestFbReauth = false;
}