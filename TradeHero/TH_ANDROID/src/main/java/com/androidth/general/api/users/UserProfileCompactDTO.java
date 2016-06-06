package com.androidth.general.api.users;

public class UserProfileCompactDTO extends UserBaseDTO
{
    public String largePicture;
    public boolean fbLinked;
    public boolean liLinked;
    public boolean qqLinked;
    public boolean thLinked;
    public boolean twLinked;
    public boolean wbLinked;

    @Override public String toString()
    {
        return "UserProfileCompactDTO{" +
                super.toString() +
                ", largePicture='" + largePicture + '\'' +
                ", fbLinked=" + fbLinked +
                ", liLinked=" + liLinked +
                ", qqLinked=" + qqLinked +
                ", thLinked=" + thLinked +
                ", twLinked=" + twLinked +
                ", wbLinked=" + wbLinked +
                '}';
    }
}
