package com.tradehero.th.persistence.prefs;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.persistence.prefs.AbstractPreference;
import com.tradehero.th.models.kyc.KYCForm;
import java.io.IOException;
import timber.log.Timber;

public class KYCFormPreference extends AbstractPreference<KYCForm>
{
    @NonNull private final ObjectMapper objectMapper;

    public KYCFormPreference(
            @NonNull ObjectMapper objectMapper,
            @NonNull SharedPreferences preference,
            @NonNull String key,
            @NonNull KYCForm defaultValue)
    {
        super(preference, key, defaultValue);
        this.objectMapper = objectMapper;
    }

    @NonNull @Override public KYCForm get()
    {
        String saved;
        try
        {
            saved = preference.getString(key, objectMapper.writeValueAsString(defaultValue));
        } catch (JsonProcessingException e)
        {
            Timber.e(e, "Failed to serialise default KYCForm");
            return defaultValue;
        }
        try
        {
            return objectMapper.readValue(saved, KYCForm.class);
        } catch (IOException e)
        {
            Timber.e(e, "Failed to deserialise KYCForm %s", saved);
            return defaultValue;
        }
    }

    @Override public void set(@NonNull KYCForm value)
    {
        try
        {
            preference.edit().putString(key, objectMapper.writeValueAsString(value)).apply();
        } catch (JsonProcessingException e)
        {
            Timber.e("Failed to serialise KYCForm %s", value);
            throw new IllegalArgumentException("Failed to serialise KYCForm");
        }
    }
}
