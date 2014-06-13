package com.tradehero.th.models.user.auth;

import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.StringPreference;
import java.text.ParseException;
import org.json.JSONException;
import timber.log.Timber;

public class MainCredentialsPreference extends StringPreference
{
    private final CredentialsDTOFactory credentialsDTOFactory;

    public MainCredentialsPreference(
            CredentialsDTOFactory credentialsDTOFactory,
            SharedPreferences preference,
            String key,
            String defaultValue)
    {
        super(preference, key, defaultValue);
        this.credentialsDTOFactory = credentialsDTOFactory;
    }

    public CredentialsDTO getCredentials()
    {
        String value = get();
        if (value == null)
        {
            return null;
        }

        try
        {
            return credentialsDTOFactory.create(value);
        }
        catch (JSONException|ParseException e)
        {
            Timber.e(e, "Failed to parse credentials string %s", value);
        }
        return null;
    }

    public void setCredentials(CredentialsDTO credentials)
    {
        if (credentials == null)
        {
            set(null);
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
