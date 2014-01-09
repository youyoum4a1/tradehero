package com.tradehero.th.api.form;

import com.tradehero.common.utils.THLog;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import org.json.JSONException;
import org.json.JSONObject;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 6:04 PM Copyright (c) TradeHero */
public class UserFormDTO
{
    private static final String TAG = UserFormDTO.class.getName();
    // Signup with email
    public String email;
    public String username;
    public String password;
    public String passwordConfirmation;
    public String firstName;
    public String lastName;
    public String displayName;

    //notifications settings
    public Boolean pushNotificationsEnabled;
    public Boolean emailNotificationsEnabled;

    // optional
    public String biography;
    public String location;
    public String website;
    public String deviceToken;

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
                ", pushNotificationsEnabled=" + pushNotificationsEnabled +
                ", emailNotificationsEnabled=" + emailNotificationsEnabled +
                ", biography='" + biography + '\'' +
                ", location='" + location + '\'' +
                ", website='" + website + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                '}';
    }
}
