package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.FacebookUserFormDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.auth.FacebookAuthenticationProvider;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import java.text.ParseException;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class FacebookCredentialsDTO extends BaseCredentialsDTO
{
    public static final String FACEBOOK_AUTH_TYPE = "TH-Facebook";

    @NotNull public final String id;
    @NotNull public final String accessToken;
    @Nullable public final Date expirationDate;

    @Nullable public static Date getExpirationDate(@NotNull JSONObject object) throws JSONException
    {
        try
        {
            return FacebookAuthenticationProvider.PRECISE_DATE_FORMAT.parse(
                    object.getString(FacebookAuthenticationProvider.EXPIRATION_DATE_KEY));
        }
        catch (ParseException e)
        {
            Timber.e(e, "Failed to parse date %s", object.getString(FacebookAuthenticationProvider.EXPIRATION_DATE_KEY));
        }
        return null;
    }

    //<editor-fold desc="Constructors">
    // TODO replace Z by +0000 in date string that comes back?
    // http://stackoverflow.com/questions/2580925/simpledateformat-parsing-date-with-z-literal
    // https://www.crashlytics.com/tradehero/android/apps/com.tradehero.th/issues/539b0912e3de5099ba5719ed
    public FacebookCredentialsDTO(@NotNull JSONObject object) throws JSONException
    {
        this(object.getString(SocialAuthenticationProvider.ID_KEY),
                object.getString(FacebookAuthenticationProvider.ACCESS_TOKEN_KEY),
                getExpirationDate(object));
    }

    public FacebookCredentialsDTO(@NotNull String id, @NotNull String accessToken, @Nullable Date expirationDate)
    {
        super();
        this.id = id;
        this.accessToken = accessToken;
        this.expirationDate = expirationDate;
    }
    //</editor-fold>

    @Override @NotNull public String getAuthType()
    {
        return FACEBOOK_AUTH_TYPE;
    }

    @Override @NotNull public String getAuthHeaderParameter()
    {
        return accessToken;
    }

    @Override protected void populate(@NotNull JSONObject object) throws JSONException
    {
        super.populate(object);
        object.put(SocialAuthenticationProvider.ID_KEY, id);
        object.put(FacebookAuthenticationProvider.ACCESS_TOKEN_KEY, accessToken);
        try
        {
            object.put(FacebookAuthenticationProvider.EXPIRATION_DATE_KEY,
                    FacebookAuthenticationProvider.PRECISE_DATE_FORMAT.format(expirationDate));
        }
        catch (IllegalArgumentException e)
        {
            Timber.e(e, "When parsing %s", expirationDate);
        }
    }

    @Override @NotNull public UserFormDTO createUserFormDTO()
    {
        FacebookUserFormDTO userFormDTO = new FacebookUserFormDTO();
        userFormDTO.accessToken = accessToken;
        return userFormDTO;
    }
}
