package com.tradehero.th.api.form;

import javax.inject.Inject;
import retrofit.mime.TypedOutput;

public class UserFormDTO
{
    public static final String KEY_TYPE = "type";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PASSWORD_CONFIRM = "confirmPassword";
    public static final String KEY_DISPLAY_NAME = "displayName";
    public static final String KEY_INVITE_CODE = "inviteCode";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_EMAIL_NOTIFICATION_ENABLED = "emailNotificationsEnabled";
    public static final String KEY_PUSH_NOTIFICATION_ENABLED = "pushNotificationsEnabled";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";

    public String email;
    public String username;
    public String password;
    public String passwordConfirmation;
    public String firstName;
    public String lastName;
    public String displayName;
    public String inviteCode;

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

    public static class Builder
    {
        private String accessToken;
        private String accessTokenSecret;

        @Inject public Builder() {}

        public Builder accessToken(String accessToken)
        {
            this.accessToken = accessToken;
            return this;
        }

        public Builder accessTokenSecret(String accessTokenSecret)
        {
            this.accessTokenSecret = accessTokenSecret;
            return this;
        }

        public UserFormDTO build()
        {
            return new UserFormDTO();
        }
    }
}
