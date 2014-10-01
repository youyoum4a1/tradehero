package com.tradehero.th.models.user.auth;

import android.util.Base64;
import com.tradehero.th.api.form.EmailUserFormDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class EmailCredentialsDTO extends BaseCredentialsDTO
{
    public static final String EMAIL_AUTH_TYPE = SocialNetworkEnum.TH.getAuthHeader();

    @NotNull public final String email;
    @NotNull public final String password;

    //<editor-fold desc="Constructors">
    public EmailCredentialsDTO(@NotNull JSONObject object) throws JSONException
    {
        this(object.getString(UserFormDTO.KEY_EMAIL),
                object.getString(UserFormDTO.KEY_PASSWORD));
    }

    public EmailCredentialsDTO(@NotNull String email, @NotNull String password)
    {
        super();
        this.email = email;
        this.password = password;
    }
    //</editor-fold>

    @Override @NotNull public String getAuthType()
    {
        return EMAIL_AUTH_TYPE;
    }

    @Override @NotNull public String getAuthHeaderParameter()
    {
        return Base64.encodeToString(
                String.format("%1$s:%2$s", email, password).getBytes(),
                Base64.NO_WRAP);
    }

    @Override protected void populate(@NotNull JSONObject object) throws JSONException
    {
        super.populate(object);
        object.put(UserFormDTO.KEY_EMAIL, email);
        object.put(UserFormDTO.KEY_PASSWORD, password);
    }

    @Override @NotNull public UserFormDTO createUserFormDTO()
    {
        UserFormDTO userFormDTO = new EmailUserFormDTO();
        userFormDTO.email = email;
        userFormDTO.password = password;
        userFormDTO.passwordConfirmation = password;
        return userFormDTO;
    }
}
