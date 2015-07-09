package com.tradehero.th.persistence.prefs;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.persistence.prefs.AbstractPreference;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import java.io.IOException;
import timber.log.Timber;

public class LiveBrokerSituationPreference extends AbstractPreference<LiveBrokerSituationDTO>
{
    @NonNull private final ObjectMapper objectMapper;

    public LiveBrokerSituationPreference(
            @NonNull ObjectMapper objectMapper,
            @NonNull SharedPreferences preference,
            @NonNull String key,
            @NonNull LiveBrokerSituationDTO defaultValue)
    {
        super(preference, key, defaultValue);
        this.objectMapper = objectMapper;
    }

    @NonNull @Override public synchronized LiveBrokerSituationDTO get()
    {
        String saved;
        try
        {
            saved = preference.getString(key, objectMapper.writeValueAsString(defaultValue));
        } catch (JsonProcessingException e)
        {
            Timber.e(e, "Failed to serialise default LiveBrokerSituationDTO");
            return defaultValue;
        }
        try
        {
            return objectMapper.readValue(saved, LiveBrokerSituationDTO.class);
        } catch (IOException e)
        {
            Timber.e(e, "Failed to deserialise LiveBrokerSituationDTO %s", saved);
            return defaultValue;
        }
    }

    @Override public synchronized void set(@NonNull LiveBrokerSituationDTO value)
    {
        try
        {
            preference.edit().putString(key, objectMapper.writeValueAsString(value)).apply();
        } catch (JsonProcessingException e)
        {
            Timber.e("Failed to serialise LiveBrokerSituationDTO %s", value);
            throw new IllegalArgumentException("Failed to serialise LiveBrokerSituationDTO");
        }
    }
}
