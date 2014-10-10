package com.tradehero.th.auth.wechat;

import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class WechatAuthData
{
    public String openid;
    public String expires_in;
    public String access_token;

    public static WechatAuthData parseAccessToken(JSONObject o)
    {
        WechatAuthData data = new WechatAuthData();
        try
        {
            data.openid = (String) o.get("openid");
            data.expires_in = String.valueOf(o.get("expires_in"));
            data.access_token = (String) o.get("access_token");
        } catch (JSONException e)
        {
            Timber.e("WechatAuthData " + e.toString());
        }
        return data;
    }
}
