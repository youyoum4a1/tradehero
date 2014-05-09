package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.UserFormFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class EmailCredentialsDTO implements CredentialsDTO
{
    public static final String EMAIL_AUTH_TYPE = "Basic";

    public final String email;
    public final String password;

    public EmailCredentialsDTO(JSONObject object) throws JSONException
    {
        this(object.getString(UserFormFactory.KEY_EMAIL), UserFormFactory.KEY_PASSWORD);
    }

    public EmailCredentialsDTO(String email, String password)
    {
        super();
        this.email = email;
        this.password = password;
    }

    @Override public String getAuthType()
    {
        return EMAIL_AUTH_TYPE;
    }
}
