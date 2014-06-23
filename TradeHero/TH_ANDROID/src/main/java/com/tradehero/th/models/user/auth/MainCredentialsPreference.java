package com.tradehero.th.models.user.auth;

import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.StringPreference;
import java.text.ParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import timber.log.Timber;

public class MainCredentialsPreference extends StringPreference
{
    @NotNull private final CredentialsDTOFactory credentialsDTOFactory;

    public MainCredentialsPreference(
            @NotNull CredentialsDTOFactory credentialsDTOFactory,
            @NotNull SharedPreferences preference,
            @NotNull String key,
            @NotNull String defaultValue)
    {
        super(preference, key, defaultValue);
        this.credentialsDTOFactory = credentialsDTOFactory;
    }

    @Nullable public CredentialsDTO getCredentials()
    {
        String value = get();
        if (!value.isEmpty())
        {
            try
            {
                return credentialsDTOFactory.create(value);
            }
            catch (JSONException|ParseException e)
            {
                Timber.e(e, "Failed to parse credentials string %s", value);
            }
        }
        return null;
    }

    public void setCredentials(@Nullable CredentialsDTO credentials)
    {
        if (credentials == null)
        {
            set("");
        }
        else
        {
            try
            {
                set(credentials.createJSON().toString());
            }
            catch (JSONException e)
            {
                Timber.e(e, "Failed to save credentials %s", credentials);
            }
        }
    }
}
