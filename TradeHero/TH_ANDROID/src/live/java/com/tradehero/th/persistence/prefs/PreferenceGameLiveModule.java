package com.tradehero.th.persistence.prefs;

import android.content.SharedPreferences;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.api.live.LiveBrokerDTO;
import com.tradehero.th.api.live.LiveBrokerId;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.kyc.EmptyKYCForm;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        complete = false,
        library = true
)
public class PreferenceGameLiveModule
{
    private static final String PREF_SAVED_BROKER_SITUATION = "PREF_SAVED_BROKER_SITUATION";
    private static final String PREF_SHOW_CALL_TO_ACTION = "PREF_SHOW_CALL_TO_ACTION";

    @Provides @Singleton LiveBrokerSituationPreference provideLiveBrokerSituationPreference(
            @ForApp ObjectMapper objectMapper,
            @ForUser SharedPreferences sharedPreferences)
    {
        return new LiveBrokerSituationPreference(objectMapper, sharedPreferences, PREF_SAVED_BROKER_SITUATION, new LiveBrokerSituationDTO(
                new LiveBrokerDTO(new LiveBrokerId(0), "Fake"), new EmptyKYCForm()));
    }

    @Provides @Singleton @ShowCallToActionFragmentPreference BooleanPreference provideShowCallToActionPreference(@ForUser SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_SHOW_CALL_TO_ACTION, true);
    }
}
