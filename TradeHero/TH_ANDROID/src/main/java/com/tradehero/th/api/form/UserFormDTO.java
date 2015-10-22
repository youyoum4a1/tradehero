package com.tradehero.th.api.form;

import retrofit.mime.TypedOutput;

public class UserFormDTO
{
    public String email;
    public String username;
    public String password;
    public String passwordConfirmation;
    public String firstName;
    public String lastName;
    public String displayName;
    public String inviteCode;
    public String phoneNumber;
    public String verifyCode;
    public String deviceAccessToken;
    public String school;
    public String signature;

    //notifications settings
    public Boolean pushNotificationsEnabled;
    public Boolean emailNotificationsEnabled;

    // optional
    public String biography;
    public String location;
    public String website;
    public String deviceToken;

    public TypedOutput profilePicture;

    public UserFormDTO()
    {
    }

    @Override public String toString()
    {
        return "UserFormDTO{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", passwordConfirmation='" + passwordConfirmation + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", inviteCode='" + inviteCode + '\'' +
                ", pushNotificationsEnabled=" + pushNotificationsEnabled +
                ", emailNotificationsEnabled=" + emailNotificationsEnabled +
                ", biography='" + biography + '\'' +
                ", location='" + location + '\'' +
                ", website='" + website + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                '}';
    }
}
