package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.FacebookUserFormDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.auth.FacebookAuthenticationProvider;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import java.text.ParseException;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookCredentialsDTO extends BaseCredentialsDTO
{
    public static final String FACEBOOK_AUTH_TYPE = "TH-Facebook";

    public final String id;
    public final String accessToken;
    public final Date expirationDate;

    //<editor-fold desc="Constructors">
    public FacebookCredentialsDTO(JSONObject object) throws JSONException, ParseException
    {
        this(object.getString(SocialAuthenticationProvider.ID_KEY),
                object.getString(FacebookAuthenticationProvider.ACCESS_TOKEN_KEY),
                FacebookAuthenticationProvider.preciseDateFormat.parse(
                        object.getString(FacebookAuthenticationProvider.EXPIRATION_DATE_KEY)));
    }

    public FacebookCredentialsDTO(String id, String accessToken, Date expirationDate)
    {
        super();
        this.id = id;
        this.accessToken = accessToken;
        this.expirationDate = expirationDate;
    }
    //</editor-fold>

    @Override public String getAuthType()
    {
        return FACEBOOK_AUTH_TYPE;
    }

    @Override protected void populate(JSONObject object) throws JSONException
    {
        super.populate(object);
        object.put(SocialAuthenticationProvider.ID_KEY, id);
        object.put(FacebookAuthenticationProvider.ACCESS_TOKEN_KEY, accessToken);
        object.put(
                FacebookAuthenticationProvider.EXPIRATION_DATE_KEY,
                FacebookAuthenticationProvider.preciseDateFormat.format(expirationDate));
    }

    @Override public UserFormDTO createUserFormDTO()
    {
        FacebookUserFormDTO userFormDTO = new FacebookUserFormDTO();
        userFormDTO.accessToken = accessToken;
        return userFormDTO;
    }
}
