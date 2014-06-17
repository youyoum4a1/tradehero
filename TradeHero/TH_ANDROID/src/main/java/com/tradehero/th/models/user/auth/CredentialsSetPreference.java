package com.tradehero.th.models.user.auth;

import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import timber.log.Timber;

public class CredentialsSetPreference extends StringSetPreference
{
    @NotNull private final CredentialsDTOFactory credentialsDTOFactory;

    public CredentialsSetPreference(
            @NotNull CredentialsDTOFactory credentialsDTOFactory,
            @NotNull SharedPreferences preference,
            @NotNull String key,
            @Nullable Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
        this.credentialsDTOFactory = credentialsDTOFactory;
    }

    public @NotNull Set<CredentialsDTO> getCredentials()
    {
        Set<CredentialsDTO> credentials = new HashSet<>();
        Set<String> stringSet = get();
        if (stringSet != null)
        {
            for (@NotNull String savedToken : stringSet)
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
        }
        return credentials;
    }

    public void setCredentials(@Nullable Set<CredentialsDTO> credentials)
    {
        Set<String> savedTokens = null;
        if (credentials != null)
        {
            savedTokens = new HashSet<>();
            for (@NotNull CredentialsDTO credentialsDTO : credentials)
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
