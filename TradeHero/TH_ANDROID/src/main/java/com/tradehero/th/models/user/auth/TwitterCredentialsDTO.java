package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.TwitterUserFormDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.TwitterAuthenticationProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class TwitterCredentialsDTO extends BaseCredentialsDTO
{
    public static final String TWITTER_AUTH_TYPE = SocialNetworkEnum.TW.getAuthHeader();

    @NotNull public final String id;
    @Nullable public String email;
    @NotNull public final String token;
    @NotNull public final String tokenSecret;
    @NotNull public final String screenName;

    //<editor-fold desc="Constructors">
    public TwitterCredentialsDTO(@NotNull JSONObject object) throws JSONException
    {
        this(object.getString(TwitterAuthenticationProvider.ID_KEY),
                object.has(TwitterAuthenticationProvider.EMAIL_KEY) ? object.getString(TwitterAuthenticationProvider.EMAIL_KEY) : "",
                object.getString(TwitterAuthenticationProvider.AUTH_TOKEN_KEY),
                object.getString(TwitterAuthenticationProvider.AUTH_TOKEN_SECRET_KEY),
                object.getString(TwitterAuthenticationProvider.SCREEN_NAME_KEY));
    }

    public TwitterCredentialsDTO(@NotNull String id,
            @Nullable String email,
            @NotNull String token,
            @NotNull String tokenSecret,
            @NotNull String screenName)
    {
        super();
        this.id = id;
        this.email = email;
        this.token = token;
        this.tokenSecret = tokenSecret;
        this.screenName = screenName;
    }
    //</editor-fold>

    @Override @NotNull public String getAuthType()
    {
        return TWITTER_AUTH_TYPE;
    }

    @Override @NotNull public String getAuthHeaderParameter()
    {
        return String.format("%1$s:%2$s", token, tokenSecret);
    }

    @Override protected void populate(@NotNull JSONObject object) throws JSONException
    {
        super.populate(object);
        object.put(TwitterAuthenticationProvider.ID_KEY, id);
        object.put(TwitterAuthenticationProvider.EMAIL_KEY, email);
        object.put(TwitterAuthenticationProvider.AUTH_TOKEN_KEY, token);
        object.put(TwitterAuthenticationProvider.AUTH_TOKEN_SECRET_KEY, tokenSecret);
        object.put(TwitterAuthenticationProvider.SCREEN_NAME_KEY, screenName);
    }

    @Override @NotNull public UserFormDTO createUserFormDTO()
    {
        TwitterUserFormDTO userFormDTO = new TwitterUserFormDTO();
        userFormDTO.accessToken = token;
        userFormDTO.accessTokenSecret = tokenSecret;
        userFormDTO.email = email;
        return userFormDTO;
    }
}
