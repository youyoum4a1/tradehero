package com.tradehero.th.api.users;

public class UserProfileCompactDTO extends UserBaseDTO implements Comparable<UserProfileCompactDTO>
{
    public String largePicture;
    public boolean fbLinked;
    public boolean liLinked;
    public boolean twLinked;
    public boolean thLinked;
    public boolean wbLinked;
    public boolean qqLinked;

    public UserProfileCompactDTO()
    {
    }

    @Override
    public int compareTo(UserProfileCompactDTO o) {
        return this.displayName.compareTo(o.displayName);
    }
}
