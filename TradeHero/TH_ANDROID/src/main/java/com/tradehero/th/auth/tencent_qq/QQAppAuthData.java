package com.tradehero.th.auth.tencent_qq;

import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

/**
 * Created by huhaiping on 14-5-26.
 */
public class QQAppAuthData {
    public String openid;
    public String expires_in;
    public String access_token;

    public static QQAppAuthData parseAccessToken(JSONObject o){
        QQAppAuthData data = new QQAppAuthData();
        try{
            data.openid = (String)o.get("openid");
            data.expires_in = (String)o.get("expires_in");
            data.access_token = (String)o.get("access_token");
         }catch (JSONException e){
            Timber.e("QQAppAuthData " + e.toString() ) ;
        }
        return data;
    }
}
