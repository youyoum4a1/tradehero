package com.tradehero.th.api.form;

import com.tradehero.th.auth.EmailAuthenticationProvider;
import com.tradehero.th.auth.FacebookAuthenticationProvider;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import org.json.JSONException;
import org.json.JSONObject;

/** Created with IntelliJ IDEA. User: xavier Date: 9/2/13 Time: 3:21 PM To change this template use File | Settings | File Templates. */
public class UserFormFactory
{
    public static final String KEY_TYPE = "type";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PASSWORD_CONFIRM = "confirmPassword";
    public static final String KEY_DISPLAY_NAME = "displayName";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";

    public static UserFormDTO create (JSONObject json) throws JSONException
    {
        String type = json.getString(KEY_TYPE);

        UserFormDTO userFormDTO = createEmptyForType(type);
        populateBase(userFormDTO, json);
        populatePerType(userFormDTO, type, json);

        return userFormDTO;
    }

    private static UserFormDTO createEmptyForType (String type)
    {
        if (type.equals(SocialAuthenticationProvider.FACEBOOK_AUTH_TYPE))
        {
            return new FacebookUserFormDTO();
        }
        if (type.equals(SocialAuthenticationProvider.LINKEDIN_AUTH_TYPE))
        {
            return new LinkedinUserFormDTO();
        }
        if (type.equals(SocialAuthenticationProvider.TWITTER_AUTH_TYPE))
        {
            return new TwitterUserFormDTO();
        }
        if (type.equals(EmailAuthenticationProvider.EMAIL_AUTH_TYPE))
        {
            return new EmailUserFormDTO();
        }
        return new UserFormDTO();
    }

    private static void populateBase (UserFormDTO userFormDTO, JSONObject json) throws JSONException
    {
        if (json.has(KEY_EMAIL))
        {
            userFormDTO.email = json.getString(KEY_EMAIL);
        }
        if (json.has(KEY_USERNAME))
        {
            userFormDTO.username = json.getString(KEY_USERNAME);
        }
        if (json.has(KEY_PASSWORD))
        {
            userFormDTO.password = json.getString(KEY_PASSWORD);
        }
        if (json.has(KEY_PASSWORD_CONFIRM))
        {
            userFormDTO.passwordConfirmation = json.getString(KEY_PASSWORD_CONFIRM);
        }
        if (json.has(KEY_DISPLAY_NAME))
        {
            userFormDTO.displayName = json.getString(KEY_DISPLAY_NAME);
        }
        if (json.has(KEY_FIRST_NAME))
        {
            userFormDTO.firstName = json.getString(KEY_FIRST_NAME);
        }
        if (json.has(KEY_LAST_NAME))
        {
            userFormDTO.lastName = json.getString(KEY_LAST_NAME);
        }
    }

    private static void populatePerType (UserFormDTO userFormDTO, String type, JSONObject json) throws JSONException
    {
        if (type.equals(SocialAuthenticationProvider.FACEBOOK_AUTH_TYPE))
        {
            ((FacebookUserFormDTO)userFormDTO).facebook_access_token = json.getString(FacebookAuthenticationProvider.ACCESS_TOKEN_KEY);
        }
        else if (type.equals(SocialAuthenticationProvider.LINKEDIN_AUTH_TYPE))
        {
            ((LinkedinUserFormDTO)userFormDTO).linkedin_access_token = json.getString(SocialAuthenticationProvider.AUTH_TOKEN_KEY);
            ((LinkedinUserFormDTO)userFormDTO).linkedin_access_token_secret = json.getString(SocialAuthenticationProvider.AUTH_TOKEN_SECRET_KEY);
        }
        else if (type.equals(SocialAuthenticationProvider.TWITTER_AUTH_TYPE))
        {
            ((TwitterUserFormDTO)userFormDTO).twitter_access_token = json.getString(SocialAuthenticationProvider.AUTH_TOKEN_KEY);
            ((TwitterUserFormDTO)userFormDTO).twitter_access_token_secret = json.getString(SocialAuthenticationProvider.AUTH_TOKEN_SECRET_KEY);
        }
    }
}
