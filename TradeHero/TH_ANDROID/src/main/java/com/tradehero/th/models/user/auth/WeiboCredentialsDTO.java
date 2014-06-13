package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.form.WeiboUserFormDTO;
import com.tradehero.th.auth.weibo.WeiboAuthenticationProvider;
import org.json.JSONException;
import org.json.JSONObject;

public class WeiboCredentialsDTO extends BaseCredentialsDTO
{
    public static final String WEIBO_AUTH_TYPE = "TH-Weibo";

    public final String uid;
    public final String token;
    public final long expiresTime;

    //<editor-fold desc="Constructors">
    public WeiboCredentialsDTO(JSONObject object) throws JSONException
    {
        this(object.getString(WeiboAuthenticationProvider.KEY_UID),
                object.getString(WeiboAuthenticationProvider.KEY_ACCESS_TOKEN),
                object.getLong(WeiboAuthenticationProvider.KEY_EXPIRES_IN));
    }

    public WeiboCredentialsDTO(String uid, String token, long expiresTime)
    {
        super();
        this.uid = uid;
        this.token = token;
        this.expiresTime = expiresTime;
    }
    //</editor-fold>

    @Override public String getAuthType()
    {
        return WEIBO_AUTH_TYPE;
    }

    @Override public String getAuthHeaderParameter()
    {
        return token;
    }

    @Override protected void populate(JSONObject object) throws JSONException
    {
        super.populate(object);
        object.put(WeiboAuthenticationProvider.KEY_UID, uid);
        object.put(WeiboAuthenticationProvider.KEY_ACCESS_TOKEN, token);
        object.put(WeiboAuthenticationProvider.KEY_EXPIRES_IN, expiresTime);
    }

    @Override public UserFormDTO createUserFormDTO()
    {
        WeiboUserFormDTO userFormDTO = new WeiboUserFormDTO();
        userFormDTO.accessToken = token;
        return userFormDTO;
    }
}
