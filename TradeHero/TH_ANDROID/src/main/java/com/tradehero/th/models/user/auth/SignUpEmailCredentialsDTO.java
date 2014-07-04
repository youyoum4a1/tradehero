package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.base.JSONCredentials;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit.mime.TypedOutput;

public class SignUpEmailCredentialsDTO extends EmailCredentialsDTO
{
    public final String displayName;
    public String firstName;
    public String lastName;
    public TypedOutput profilePicture;

    public SignUpEmailCredentialsDTO(JSONObject object) throws JSONException
    {
        super(object);
        this.displayName = object.getString(UserFormFactory.KEY_DISPLAY_NAME);
        if (object.has(UserFormFactory.KEY_FIRST_NAME))
        {
            firstName = object.getString(UserFormFactory.KEY_FIRST_NAME);
        }
        if (object.has(UserFormFactory.KEY_LAST_NAME))
        {
            lastName = object.getString(UserFormFactory.KEY_LAST_NAME);
        }
        if (object instanceof JSONCredentials)
        {
            profilePicture = ((JSONCredentials)object).profilePicture;
        }
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
        object.put(UserFormFactory.KEY_FIRST_NAME, firstName);
        object.put(UserFormFactory.KEY_LAST_NAME, lastName);
    }

    @Override public UserFormDTO createUserFormDTO()
    {
        UserFormDTO userFormDTO = super.createUserFormDTO();
        userFormDTO.displayName = displayName;
        userFormDTO.firstName = firstName;
        userFormDTO.lastName = lastName;
        userFormDTO.profilePicture = profilePicture;
        return userFormDTO;
    }
}
