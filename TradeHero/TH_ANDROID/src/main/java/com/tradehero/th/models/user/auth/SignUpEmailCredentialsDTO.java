package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.form.UserFormFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class SignUpEmailCredentialsDTO extends EmailCredentialsDTO
{
    public final String displayName;

    public SignUpEmailCredentialsDTO(JSONObject object) throws JSONException
    {
        super(object);
        this.displayName = object.getString(UserFormFactory.KEY_DISPLAY_NAME);
    }

    public SignUpEmailCredentialsDTO(String email, String password, String displayName)
    {
        super(email, password);
        this.displayName = displayName;
    }

    @Override protected void populate(JSONObject object) throws JSONException
    {
        super.populate(object);
        object.put(UserFormFactory.KEY_DISPLAY_NAME, displayName);
    }

    @Override public UserFormDTO createUserFormDTO()
    {
        UserFormDTO userFormDTO = super.createUserFormDTO();
        userFormDTO.displayName = displayName;
        return userFormDTO;
    }
}
