package com.tradehero.th.models.user.auth;

import org.json.JSONException;
import org.json.JSONObject;

public interface CredentialsDTO
{
    String getAuthType();
    JSONObject createJSON() throws JSONException;
}
