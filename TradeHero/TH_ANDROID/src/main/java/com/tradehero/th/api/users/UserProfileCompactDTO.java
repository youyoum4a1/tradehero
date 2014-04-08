package com.tradehero.th.api.users;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 7:02 PM Copyright (c) TradeHero */
public class UserProfileCompactDTO extends UserBaseDTO
{
    public static final String TAG = UserProfileCompactDTO.class.getName();

    public String largePicture;
    public boolean fbLinked;
    public boolean liLinked;
    public boolean twLinked;
    public boolean thLinked;

    public UserProfileCompactDTO()
    {
    }
}
