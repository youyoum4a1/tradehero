package com.tradehero.th.api.users;


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
