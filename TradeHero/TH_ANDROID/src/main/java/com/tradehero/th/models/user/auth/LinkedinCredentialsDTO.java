package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.LinkedinUserFormDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.linkedin.LinkedInAuthenticationProvider;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class LinkedinCredentialsDTO extends BaseCredentialsDTO
{
    public static final String LINKEDIN_AUTH_TYPE = SocialNetworkEnum.LN.getAuthHeader();

    @NotNull public final String token;
    @NotNull public final String tokenSecret;
    @NotNull public final String consumerKey;
    @NotNull public final String consumerSecretKey;

    //<editor-fold desc="Constructors">
    public LinkedinCredentialsDTO(@NotNull JSONObject object) throws JSONException
    {
        this(object.getString(LinkedInAuthenticationProvider.AUTH_TOKEN_KEY),
                object.getString(LinkedInAuthenticationProvider.AUTH_TOKEN_SECRET_KEY),
                object.getString(LinkedInAuthenticationProvider.CONSUMER_KEY_KEY),
                object.getString(LinkedInAuthenticationProvider.CONSUMER_SECRET_KEY));
    }

    public LinkedinCredentialsDTO(@NotNull String token,
            @NotNull String tokenSecret,
            @NotNull String consumerKey,
            @NotNull String consumerSecretKey)
    {
        super();
        this.token = token;
        this.tokenSecret = tokenSecret;
        this.consumerKey = consumerKey;
        this.consumerSecretKey = consumerSecretKey;
    }
    //</editor-fold>

    @Override @NotNull public String getAuthType()
    {
        return LINKEDIN_AUTH_TYPE;
    }

    @Override @NotNull public String getAuthHeaderParameter()
    {
        return String.format("%1$s:%2$s", token, tokenSecret);
    }

    @Override protected void populate(@NotNull JSONObject object) throws JSONException
    {
        super.populate(object);
        object.put(LinkedInAuthenticationProvider.AUTH_TOKEN_KEY, token);
        object.put(LinkedInAuthenticationProvider.AUTH_TOKEN_SECRET_KEY, tokenSecret);
        object.put(LinkedInAuthenticationProvider.CONSUMER_KEY_KEY, consumerKey);
        object.put(LinkedInAuthenticationProvider.CONSUMER_SECRET_KEY, consumerSecretKey);
    }

    @Override @NotNull public UserFormDTO createUserFormDTO()
    {
        LinkedinUserFormDTO userFormDTO = new LinkedinUserFormDTO();
        userFormDTO.accessToken = token;
        userFormDTO.accessTokenSecret = tokenSecret;
        return userFormDTO;
    }
}
