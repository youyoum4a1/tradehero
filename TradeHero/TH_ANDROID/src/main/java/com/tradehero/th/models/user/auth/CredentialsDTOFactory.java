package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.UserFormFactory;
import java.text.ParseException;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;

public class CredentialsDTOFactory
{
    @Inject public CredentialsDTOFactory()
    {
        super();
    }

    public CredentialsDTO create(String savedToken) throws JSONException, ParseException
    {
        return create(new JSONObject(savedToken));
    }

    public CredentialsDTO create(JSONObject object) throws JSONException, ParseException
    {
        CredentialsDTO created;
        String type = object.getString(UserFormFactory.KEY_TYPE);
        switch(type)
        {
            case EmailCredentialsDTO.EMAIL_AUTH_TYPE:
                created = new EmailCredentialsDTO(object);
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
}
