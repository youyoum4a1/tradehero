package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.base.JSONCredentials;
import org.json.JSONException;

public interface CredentialsDTO
{
    String getAuthType();
    JSONCredentials createJSON() throws JSONException; // TODO make it a simple JSONObject
    UserFormDTO createUserFormDTO();
}
