package com.tradehero.th.auth;

import org.json.JSONObject;

public interface THAuthenticationProvider
    extends AuthenticationProvider
{
    public void cancel();

    public static interface THAuthenticationCallback
    {
        public void onStart();

        public void onSuccess(JSONObject paramJSONObject);

        public void onCancel();

        public void onError(Throwable paramThrowable);
    }
}