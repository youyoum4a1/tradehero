package com.tradehero.th.api.users;

/** Created with IntelliJ IDEA. User: tho Date: 9/4/13 Time: 11:27 AM Copyright (c) TradeHero */
public class UserLoginDTO
{
    public static final String SUGGEST_UPGRADE = UserLoginDTO.class.getName() + ".suggestUpgrade";
    public static final String SUGGEST_LI_REAUTH = UserLoginDTO.class.getName() + ".suggestLiReauth";
    public static final String SUGGEST_TW_REAUTH = UserLoginDTO.class.getName() + ".suggestTwReauth";
    public static final String SUGGEST_FB_REAUTH = UserLoginDTO.class.getName() + ".suggestFbReauth";

    public UserProfileDTO profileDTO;
    public boolean suggestUpgrade = false; // if true, client will suggest user upgrades client

    public boolean suggestLiReauth = false;
    public boolean suggestTwReauth = false;
    public boolean suggestFbReauth = false;
}