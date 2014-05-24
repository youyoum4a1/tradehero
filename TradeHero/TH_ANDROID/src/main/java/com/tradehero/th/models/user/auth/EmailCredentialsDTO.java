package com.tradehero.th.models.user.auth;

import android.util.Base64;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.form.UserFormFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class EmailCredentialsDTO extends BaseCredentialsDTO
{
    public static final String EMAIL_AUTH_TYPE = "Basic";

    public final String email;
    public final String password;

    //<editor-fold desc="Constructors">
    public EmailCredentialsDTO(JSONObject object) throws JSONException
    {
        this(object.getString(UserFormFactory.KEY_EMAIL),
                object.getString(UserFormFactory.KEY_PASSWORD));
    }

    public EmailCredentialsDTO(String email, String password)
    {
        super();
        this.email = email;
        this.password = password;
    }
    //</editor-fold>

    @Override public String getAuthType()
    {
        return EMAIL_AUTH_TYPE;
    }

    @Override public String getAuthHeaderParameter()
    {
        return  Base64.encodeToString(
                String.format("%1$s:%2$s", email, password).getBytes(),
                Base64.NO_WRAP);
    }

    @Override protected void populate(JSONObject object) throws JSONException
    {
        super.populate(object);
        object.put(UserFormFactory.KEY_EMAIL, email);
        object.put(UserFormFactory.KEY_PASSWORD, password);
    }

    @Override public UserFormDTO createUserFormDTO()
    {
        UserFormDTO userFormDTO = new UserFormDTO();
        userFormDTO.email = email;
        userFormDTO.password = password;
        userFormDTO.passwordConfirmation = password;
        return userFormDTO;
    }
}
