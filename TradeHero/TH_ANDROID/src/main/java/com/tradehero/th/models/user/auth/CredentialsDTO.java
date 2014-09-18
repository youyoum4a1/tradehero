package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.base.JSONCredentials;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

public interface CredentialsDTO
{
    @NotNull String getAuthType();
    @NotNull String getAuthHeaderParameter();
    @NotNull JSONCredentials createJSON() throws JSONException; // TODO make it a simple JSONObject
    @NotNull UserFormDTO createUserFormDTO();
}
