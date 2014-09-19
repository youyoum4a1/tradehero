package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.base.JSONCredentials;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

abstract public class BaseCredentialsDTO implements CredentialsDTO
{
    //<editor-fold desc="Constructors">
    public BaseCredentialsDTO()
    {
        super();
    }
    //</editor-fold>

    @Override @NotNull public JSONCredentials createJSON() throws JSONException
    {
        JSONCredentials created = new JSONCredentials();
        populate(created);
        return created;
    }

    protected void populate(@NotNull JSONObject object) throws JSONException
    {
        object.put(UserFormFactory.KEY_TYPE, getAuthType());
    }
}
