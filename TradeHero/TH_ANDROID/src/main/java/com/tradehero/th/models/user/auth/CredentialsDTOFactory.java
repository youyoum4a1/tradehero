package com.tradehero.th.models.user.auth;

import android.text.TextUtils;
import android.util.Base64;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.form.UserFormFactory;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.text.ParseException;

public class CredentialsDTOFactory
{
    @Inject public CredentialsDTOFactory()
    {
        super();
    }

    @NotNull public CredentialsDTO create(@NotNull String savedToken) throws JSONException, ParseException
    {
        return create(new JSONObject(savedToken));
    }

    @NotNull public CredentialsDTO create(@NotNull JSONObject object) throws JSONException, ParseException
    {
        CredentialsDTO created;
        String type = object.getString(UserFormFactory.KEY_TYPE);
        switch(type)
        {
            case EmailCredentialsDTO.EMAIL_AUTH_TYPE:
                if (object.has(UserFormFactory.KEY_DISPLAY_NAME))
                {
                    created = new SignUpEmailCredentialsDTO(object);
                }
                else
                {
                    created = new EmailCredentialsDTO(object);
                }
                break;


            // TODO WeChat

            case QQCredentialsDTO.QQ_AUTH_TYPE:
                created = new QQCredentialsDTO(object);
                break;

            case WeChatCredentialsDTO.WECHAT_AUTH_TYPE:
                created = new WeChatCredentialsDTO(object);
                break;

            case WeiboCredentialsDTO.WEIBO_AUTH_TYPE:
                created = new WeiboCredentialsDTO(object);
                break;
            case DeviceCredentialsDTO.DEVICE_AUTH_TYPE:
                created = new DeviceCredentialsDTO(object);
                break;
            default:
                throw new IllegalArgumentException("Unhandled type " + type);
        }
        return created;
    }

    @Deprecated
    public CredentialsDTO createFromOldSessionToken(String oldType, StringPreference oldTokenPref)
    {
        if (oldType != null && oldTokenPref != null)
        {
            String authToken = oldTokenPref.get();
            if (!TextUtils.isEmpty(authToken))
            {
                switch(oldType)
                {
                    case EmailCredentialsDTO.EMAIL_AUTH_TYPE:
                        String decoded = new String(
                                Base64.decode(authToken.getBytes(), Base64.NO_WRAP));
                        if (!TextUtils.isEmpty(decoded))
                        {
                            String[] emailPass = decoded.split(":");
                            if (emailPass.length == 2)
                            {
                                return new EmailCredentialsDTO(emailPass[0], emailPass[1]);
                            }
                        }
                        break;
                }
            }
        }
        return null;
    }
}
