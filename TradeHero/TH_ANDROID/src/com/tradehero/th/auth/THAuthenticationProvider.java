package com.tradehero.th.auth;

import org.json.JSONObject;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 2:45 PM Copyright (c) TradeHero */
public interface THAuthenticationProvider
{
    public void authenticate(THAuthenticationCallback callback);

    public void deauthenticate();

    public boolean restoreAuthentication(JSONObject paramJSONObject);

    public void cancel();

    public String getAuthType();

    public static interface THAuthenticationCallback
    {
        public void onStart();

        public void onSuccess(JSONObject paramJSONObject);

        public void onCancel();

        public void onError(Throwable paramThrowable);
    }
}