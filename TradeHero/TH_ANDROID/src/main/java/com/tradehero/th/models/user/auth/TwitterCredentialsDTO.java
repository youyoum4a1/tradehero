package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.TwitterUserFormDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.auth.TwitterAuthenticationProvider;
import org.json.JSONException;
import org.json.JSONObject;

public class TwitterCredentialsDTO extends BaseCredentialsDTO
{
    public static final String TWITTER_AUTH_TYPE = "TH-Twitter";

    public final String id;
    public String email;
    public final String token;
    public final String tokenSecret;
    public final String screenName;

    //<editor-fold desc="Constructors">
    public TwitterCredentialsDTO(JSONObject object) throws JSONException
    {
        this(object.getString(TwitterAuthenticationProvider.ID_KEY),
                object.getString(TwitterAuthenticationProvider.EMAIL_KEY),
                object.getString(TwitterAuthenticationProvider.AUTH_TOKEN_KEY),
                object.getString(TwitterAuthenticationProvider.AUTH_TOKEN_SECRET_KEY),
                object.getString(TwitterAuthenticationProvider.SCREEN_NAME_KEY));
    }

    public TwitterCredentialsDTO(String id, String email, String token, String tokenSecret, String screenName)
    {
        super();
        this.id = id;
        this.email = email;
        this.token = token;
        this.tokenSecret = tokenSecret;
        this.screenName = screenName;
    }
    //</editor-fold>

    @Override public String getAuthType()
    {
        return TWITTER_AUTH_TYPE;
    }

    @Override protected void populate(JSONObject object) throws JSONException
    {
        super.populate(object);
        object.put(TwitterAuthenticationProvider.ID_KEY, id);
        object.put(TwitterAuthenticationProvider.EMAIL_KEY, email);
        object.put(TwitterAuthenticationProvider.AUTH_TOKEN_KEY, token);
        object.put(TwitterAuthenticationProvider.AUTH_TOKEN_SECRET_KEY, tokenSecret);
        object.put(TwitterAuthenticationProvider.SCREEN_NAME_KEY, screenName);
    }

    @Override public UserFormDTO createUserFormDTO()
    {
        TwitterUserFormDTO userFormDTO = new TwitterUserFormDTO();
        userFormDTO.accessToken = token;
        userFormDTO.accessTokenSecret = tokenSecret;
        return userFormDTO;
    }
}
