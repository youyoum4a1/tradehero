package com.tradehero.th.auth;

import org.json.JSONObject;


public interface THAuthenticationProvider
{
    public void authenticate(THAuthenticationCallback callback);

    public void deauthenticate();

    /**
     * Provides the opportunity to populate missing fields in the authentication object.
     * Typically, those would come from the local storage.
     * @param paramJSONObject
     * @return success
     */
    public boolean restoreAuthentication(JSONObject paramJSONObject);

    public void cancel();

    public String getAuthType();

    String getAuthHeader();

    String getAuthHeaderParameter ();

    public static interface THAuthenticationCallback
    {
        public void onStart();

        public void onSuccess(JSONObject paramJSONObject);

        public void onCancel();

        public void onError(Throwable paramThrowable);
    }
}