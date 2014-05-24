package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.QQUserFormDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import java.text.ParseException;
import org.json.JSONException;
import org.json.JSONObject;

public class QQCredentialsDTO extends BaseCredentialsDTO
{
    public static final String QQ_AUTH_TYPE = "TH-QQ";

    public static final String KEY_ACCESS_TOKEN = "access_token";

    public final String openId;
    public final String accessToken;

    //<editor-fold desc="Constructors">
    public QQCredentialsDTO(JSONObject object) throws JSONException, ParseException
    {
        this(object.getString(SocialAuthenticationProvider.ID_KEY),
                object.getString(KEY_ACCESS_TOKEN));
    }

    public QQCredentialsDTO(String openId, String accessToken)
    {
        super();
        this.openId = openId;
        this.accessToken = accessToken;
    }
    //</editor-fold>

    @Override public String getAuthType()
    {
        return QQ_AUTH_TYPE;
    }

    @Override protected void populate(JSONObject object) throws JSONException
    {
        super.populate(object);
        object.put(SocialAuthenticationProvider.ID_KEY, openId);
        object.put(KEY_ACCESS_TOKEN, accessToken);
    }

    @Override public UserFormDTO createUserFormDTO()
    {
        QQUserFormDTO userFormDTO = new QQUserFormDTO();
        userFormDTO.accessToken = accessToken;
        return userFormDTO;
    }}
