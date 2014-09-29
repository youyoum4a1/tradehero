package com.tradehero.th.models.user.auth;

import android.text.TextUtils;
import android.util.Base64;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.social.SocialNetworkEnum;
import java.text.ParseException;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class CredentialsDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public CredentialsDTOFactory()
    {
        super();
    }
    //</editor-fold>

    @NotNull public CredentialsDTO create(@NotNull String savedToken) throws JSONException, ParseException
    {
        return create(new JSONObject(savedToken));
    }

    @NotNull public CredentialsDTO create(@NotNull JSONObject object) throws JSONException, ParseException
    {
        CredentialsDTO created;
        String type = object.getString(UserFormFactory.KEY_TYPE);
        SocialNetworkEnum networkType = SocialNetworkEnum.fromAuthHeader(type);
        switch(networkType)
        {
            case TH:
                if (object.has(UserFormFactory.KEY_DISPLAY_NAME))
                {
                    created = new SignUpEmailCredentialsDTO(object);
                }
                else
                {
                    created = new EmailCredentialsDTO(object);
                }
                break;

            case FB:
                created = new FacebookCredentialsDTO(object);
                break;

            case LN:
                created = new LinkedinCredentialsDTO(object);
                break;

            case TW:
                created = new TwitterCredentialsDTO(object);
                break;

            // TODO WeChat

            case QQ:
                created = new QQCredentialsDTO(object);
                break;

            case WB:
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
                SocialNetworkEnum socialNetworkEnum = SocialNetworkEnum.fromAuthHeader(oldType);
                switch(socialNetworkEnum)
                {
                    case TH:
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
