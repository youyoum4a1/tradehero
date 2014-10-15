package com.tradehero.th.auth.tencent_qq;

import org.json.JSONException;
import org.json.JSONObject;

public class QQAppAuthData
{
    public static final String JSON_KEY_OPEN_ID = "openid";
    public static final String JSON_KEY_EXPIRES_IN = "expires_in";
    public static final String JSON_KEY_ACCESS_TOKEN = "access_token";

    public String openId;
    public String expiresIn;
    public String accessToken;

    //<editor-fold desc="Constructors">
    public QQAppAuthData(Object object) throws JSONException
    {
        this(object.toString());
    }

    public QQAppAuthData(String object) throws JSONException
    {
        this(new JSONObject(object));
    }

    public QQAppAuthData(JSONObject jsonObject) throws JSONException
    {
        openId = (String) jsonObject.get(JSON_KEY_OPEN_ID);
        expiresIn = String.valueOf(jsonObject.get(JSON_KEY_EXPIRES_IN));
        accessToken = (String) jsonObject.get(JSON_KEY_ACCESS_TOKEN);
    }
    //</editor-fold>
}
