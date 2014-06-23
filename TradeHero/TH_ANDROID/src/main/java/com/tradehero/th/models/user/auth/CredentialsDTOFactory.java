package com.tradehero.th.models.user.auth;

import android.text.TextUtils;
import android.util.Base64;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.form.UserFormFactory;
import java.text.ParseException;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

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

            case FacebookCredentialsDTO.FACEBOOK_AUTH_TYPE:
                created = new FacebookCredentialsDTO(object);
                break;

            case LinkedinCredentialsDTO.LINKEDIN_AUTH_TYPE:
                created = new LinkedinCredentialsDTO(object);
                break;

            case TwitterCredentialsDTO.TWITTER_AUTH_TYPE:
                created = new TwitterCredentialsDTO(object);
                break;

            // TODO WeChat

            case QQCredentialsDTO.QQ_AUTH_TYPE:
                created = new QQCredentialsDTO(object);
                break;

            case WeiboCredentialsDTO.WEIBO_AUTH_TYPE:
                created = new WeiboCredentialsDTO(object);
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
