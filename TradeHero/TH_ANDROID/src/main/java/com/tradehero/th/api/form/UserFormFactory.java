package com.tradehero.th.api.form;

import com.tradehero.th.auth.tencent_qq.QQAuthenticationProvider;
import com.tradehero.th.auth.weibo.WeiboAuthenticationProvider;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.models.user.auth.DeviceCredentialsDTO;
import com.tradehero.th.models.user.auth.EmailCredentialsDTO;
import com.tradehero.th.models.user.auth.QQCredentialsDTO;
import com.tradehero.th.models.user.auth.WeiboCredentialsDTO;
import org.json.JSONException;
import org.json.JSONObject;

public class UserFormFactory
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
    public static final String KEY_PROFILE_PICTURE = "profilePicture";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_VERIFY_CODE = "verifyCode";

    public static UserFormDTO create(JSONCredentials json)
    {
        try
        {
            String type = json.getString(KEY_TYPE);

            UserFormDTO userFormDTO = createEmptyForType(type);
            populateBase(userFormDTO, json);
            populatePerType(userFormDTO, type, json);
            return userFormDTO;
        } catch (JSONException ex)
        {
            return null;
        }
    }

    private static UserFormDTO createEmptyForType(String type)
    {

        if (type.equals(EmailCredentialsDTO.EMAIL_AUTH_TYPE))
        {
            return new EmailUserFormDTO();
        }
        if (type.equals(QQCredentialsDTO.QQ_AUTH_TYPE))
        {
            return new QQUserFormDTO();
        }
        if (type.equals(WeiboCredentialsDTO.WEIBO_AUTH_TYPE))
        {
            return new WeiboUserFormDTO();
        }
        if (type.equals(DeviceCredentialsDTO.DEVICE_AUTH_TYPE))
        {
            return new DeviceUserFormDTO();
        }

        return new UserFormDTO();
    }

    private static void populateBase(UserFormDTO userFormDTO, JSONCredentials json) throws JSONException
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
        if (json.profilePicture != null)
        {
            userFormDTO.profilePicture = json.profilePicture;
        }
        if (json.has(KEY_PHONE_NUMBER))
        {
            userFormDTO.phoneNumber = json.getString(KEY_PHONE_NUMBER);
        }
        if (json.has(KEY_VERIFY_CODE))
        {
            userFormDTO.verifyCode = json.getString(KEY_VERIFY_CODE);
        }
    }

    private static void populatePerType(UserFormDTO userFormDTO, String type, JSONObject json)
            throws JSONException
    {
        if (type.equals(QQCredentialsDTO.QQ_AUTH_TYPE))
        {
            ((QQUserFormDTO) userFormDTO).openid = json.getString(QQAuthenticationProvider.KEY_OPEN_ID);
            ((QQUserFormDTO) userFormDTO).accessToken = json.getString(QQAuthenticationProvider.KEY_ACCESS_TOKEN);
        }
        else if (type.equals(WeiboCredentialsDTO.WEIBO_AUTH_TYPE))
        {
            ((WeiboUserFormDTO) userFormDTO).accessToken =
                    json.getString(WeiboAuthenticationProvider.KEY_ACCESS_TOKEN);
        }
    }
}
