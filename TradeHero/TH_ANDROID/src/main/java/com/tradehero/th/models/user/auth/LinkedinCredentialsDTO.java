package com.tradehero.th.models.user.auth;

import com.tradehero.th.auth.LinkedInAuthenticationProvider;
import org.json.JSONException;
import org.json.JSONObject;

public class LinkedinCredentialsDTO extends BaseCredentialsDTO
{
    public static final String LINKEDIN_AUTH_TYPE = "TH-LinkedIn";

    public final String token;
    public final String tokenSecret;
    public final String consumerKey;
    public final String consumerSecretKey;

    //<editor-fold desc="Constructors">
    public LinkedinCredentialsDTO(JSONObject object) throws JSONException
    {
        this(object.getString(LinkedInAuthenticationProvider.AUTH_TOKEN_KEY),
                object.getString(LinkedInAuthenticationProvider.AUTH_TOKEN_SECRET_KEY),
                object.getString(LinkedInAuthenticationProvider.CONSUMER_KEY_KEY),
                object.getString(LinkedInAuthenticationProvider.CONSUMER_SECRET_KEY));
    }

    public LinkedinCredentialsDTO(String token, String tokenSecret, String consumerKey, String consumerSecretKey)
    {
        super();
        this.token = token;
        this.tokenSecret = tokenSecret;
        this.consumerKey = consumerKey;
        this.consumerSecretKey = consumerSecretKey;
    }
    //</editor-fold>

    @Override public String getAuthType()
    {
        return LINKEDIN_AUTH_TYPE;
    }

    @Override protected void populate(JSONObject object) throws JSONException
    {
        super.populate(object);
        object.put(LinkedInAuthenticationProvider.AUTH_TOKEN_KEY, token);
        object.put(LinkedInAuthenticationProvider.AUTH_TOKEN_SECRET_KEY, tokenSecret);
        object.put(LinkedInAuthenticationProvider.CONSUMER_KEY_KEY, consumerKey);
        object.put(LinkedInAuthenticationProvider.CONSUMER_SECRET_KEY, consumerSecretKey);
    }
}
