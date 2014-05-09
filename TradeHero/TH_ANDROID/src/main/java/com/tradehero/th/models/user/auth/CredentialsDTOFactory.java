package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.UserFormFactory;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;

public class CredentialsDTOFactory
{
    @Inject public CredentialsDTOFactory()
    {
        super();
    }

    public CredentialsDTO create(String savedToken) throws JSONException
    {
        return create(new JSONObject(savedToken));
    }

    public CredentialsDTO create(JSONObject object) throws JSONException
    {
        CredentialsDTO created;
        String type = object.getString(UserFormFactory.KEY_TYPE);
        switch(type)
        {
            case EmailCredentialsDTO.EMAIL_AUTH_TYPE:
                created = new EmailCredentialsDTO(object);
                break;

            // TODO others

            default:
                throw new IllegalArgumentException("Unhandled type " + type);
        }
        return created;
    }
}
