package com.tradehero.th.persistence.prefs;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.persistence.prefs.AbstractPreference;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import java.io.IOException;
import javax.inject.Singleton;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

@Singleton
public class LiveBrokerSituationPreference extends AbstractPreference<LiveBrokerSituationDTO>
{
    @NonNull private final ObjectMapper objectMapper;
    @NonNull private final PublishSubject<LiveBrokerSituationDTO> liveBrokerSituationDTOPublishSubject;
    private Observable<LiveBrokerSituationDTO> liveBrokerSituationDTOObservable;

    public LiveBrokerSituationPreference(
            @NonNull ObjectMapper objectMapper,
            @NonNull SharedPreferences preference,
            @NonNull String key,
            @NonNull LiveBrokerSituationDTO defaultValue)
    {
        super(preference, key, defaultValue);
        this.objectMapper = objectMapper;
        liveBrokerSituationDTOPublishSubject = PublishSubject.create();
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
        LiveBrokerSituationDTO saved = get();
        if (saved.kycForm != null && value.kycForm != null)
        {
            if (saved.kycForm.getClass().equals(value.kycForm.getClass()))
            {
                saved.kycForm.pickFrom(value.kycForm);
            }
            else
            {
                // TODO review when we allow more than 1 broker / country
                saved = value;
            }
        }
        try
        {
            Timber.d("saved fullname: %s", ((KYCAyondoForm) saved.kycForm).getFullName());
            preference.edit().putString(key, objectMapper.writeValueAsString(saved)).apply();
            liveBrokerSituationDTOPublishSubject.onNext(saved);
        } catch (JsonProcessingException e)
        {
            Timber.e("Failed to serialise LiveBrokerSituationDTO %s", saved);
            throw new IllegalArgumentException("Failed to serialise LiveBrokerSituationDTO");
        }
    }

    @NonNull public Observable<LiveBrokerSituationDTO> getLiveBrokerSituationDTOObservable()
    {
        if (liveBrokerSituationDTOObservable == null)
        {
            liveBrokerSituationDTOObservable = liveBrokerSituationDTOPublishSubject.startWith(get()).distinctUntilChanged();
        }
        return liveBrokerSituationDTOObservable;
    }
}
