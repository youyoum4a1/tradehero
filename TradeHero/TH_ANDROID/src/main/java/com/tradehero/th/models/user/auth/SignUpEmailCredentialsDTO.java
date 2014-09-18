package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.base.JSONCredentials;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit.mime.TypedOutput;

public class SignUpEmailCredentialsDTO extends EmailCredentialsDTO
{
    @NotNull public final String displayName;
    @Nullable public String inviteCode;
    @Nullable public String firstName;
    @Nullable public String lastName;
    @Nullable public TypedOutput profilePicture;

    //<editor-fold desc="Constructors">
    public SignUpEmailCredentialsDTO(@NotNull JSONObject object) throws JSONException
    {
        super(object);
        displayName = object.getString(UserFormFactory.KEY_DISPLAY_NAME);
        if (object.has(UserFormFactory.KEY_INVITE_CODE))
        {
            inviteCode = object.getString(UserFormFactory.KEY_INVITE_CODE);
        }
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

    public SignUpEmailCredentialsDTO(@NotNull String email, @NotNull String password, @NotNull String displayName)
    {
        super(email, password);
        this.displayName = displayName;
    }
    //</editor-fold>

    @Override protected void populate(@NotNull JSONObject object) throws JSONException
    {
        super.populate(object);
        object.put(UserFormFactory.KEY_DISPLAY_NAME, displayName);
        object.put(UserFormFactory.KEY_INVITE_CODE, inviteCode);
        object.put(UserFormFactory.KEY_FIRST_NAME, firstName);
        object.put(UserFormFactory.KEY_LAST_NAME, lastName);
    }

    @Override @NotNull public UserFormDTO createUserFormDTO()
    {
        UserFormDTO userFormDTO = super.createUserFormDTO();
        userFormDTO.displayName = displayName;
        userFormDTO.inviteCode = inviteCode;
        userFormDTO.firstName = firstName;
        userFormDTO.lastName = lastName;
        userFormDTO.profilePicture = profilePicture;
        return userFormDTO;
    }
}
