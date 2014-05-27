package com.tradehero.th.models.user.auth;

import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONException;
import timber.log.Timber;

public class CredentialsSetPreference extends StringSetPreference
{
    private final CredentialsDTOFactory credentialsDTOFactory;

    public CredentialsSetPreference(
            CredentialsDTOFactory credentialsDTOFactory,
            SharedPreferences preference,
            String key,
            Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
        this.credentialsDTOFactory = credentialsDTOFactory;
    }

    public Set<CredentialsDTO> getCredentials()
    {
        Set<CredentialsDTO> credentials = new HashSet<>();
        for (String savedToken : get())
        {
            try
            {
                credentials.add(credentialsDTOFactory.create(savedToken));
            }
            catch (JSONException|ParseException e)
            {
                Timber.e(e, "Parsing savedToken: %s", savedToken);
            }
        }
        return credentials;
    }

    public void setCredentials(Set<CredentialsDTO> credentials)
    {
        Set<String> savedTokens = null;
        if (credentials != null)
        {
            savedTokens = new HashSet<>();
            for (CredentialsDTO credentialsDTO : credentials)
            {
                try
                {
                    savedTokens.add(credentialsDTO.createJSON().toString());
                }
                catch (JSONException e)
                {
                    Timber.e(e, "Failed to save credentials %s", credentialsDTO);
                }
            }
        }
        set(savedTokens);
    }

    public void replaceOrAddCredentials(CredentialsDTO credentialsDTO)
    {
        Set<CredentialsDTO> credentials = getCredentials();
        for (CredentialsDTO existingCredentialsDTO : new HashSet<>(credentials))
        {
            if (existingCredentialsDTO.getAuthType().equals(credentialsDTO.getAuthType()))
            {
                credentials.remove(existingCredentialsDTO);
            }
        }
        credentials.add(credentialsDTO);
        setCredentials(credentials);
    }

    public void replaceOrSkipCredentials(CredentialsDTO credentialsDTO)
    {
        Set<CredentialsDTO> credentials = getCredentials();
        for (CredentialsDTO existingCredentialsDTO : new HashSet<>(credentials))
        {
            if (existingCredentialsDTO.getAuthType().equals(credentialsDTO.getAuthType()))
            {
                credentials.remove(existingCredentialsDTO);
                credentials.add(credentialsDTO);
                break;
            }
        }
        setCredentials(credentials);
    }
}
