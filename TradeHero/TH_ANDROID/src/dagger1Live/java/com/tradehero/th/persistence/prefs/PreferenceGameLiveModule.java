package com.ayondo.academy.persistence.prefs;

import android.content.SharedPreferences;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.ayondo.academy.api.kyc.EmptyKYCForm;
import com.ayondo.academy.api.kyc.KYCForm;
import com.ayondo.academy.api.kyc.StepStatus;
import com.ayondo.academy.api.live.LiveBrokerDTO;
import com.ayondo.academy.api.live.LiveBrokerId;
import com.ayondo.academy.api.live.LiveBrokerSituationDTO;
import dagger.Module;
import dagger.Provides;
import java.util.ArrayList;
import java.util.HashSet;
import javax.inject.Singleton;

@Module(
        complete = false,
        library = true
)
public class PreferenceGameLiveModule
{
    private static final String PREF_SAVED_BROKER_SITUATION = "PREF_SAVED_BROKER_SITUATION";
    private static final String PREF_SAVED_VERIFIED_PHONE_NUMBERS = "PREF_SAVED_VERIFIED_PHONE_NUMBERS";
    private static final String PREF_SHOW_CALL_TO_ACTION = "PREF_SHOW_CALL_TO_ACTION";
    private static final String PREF_LIVE_AVAILABILITY = "PREF_LIVE_AVAILABILITY";

    @Provides @Singleton LiveBrokerSituationPreference provideLiveBrokerSituationPreference(
            ObjectMapper objectMapper, // Do not use @ForApp because it removes the Jackson visibility of methods
            @ForUser SharedPreferences sharedPreferences)
    {
        KYCForm empty = new EmptyKYCForm();
        empty.setStepStatuses(new ArrayList<StepStatus>());
        return new LiveBrokerSituationPreference(objectMapper, sharedPreferences, PREF_SAVED_BROKER_SITUATION, new LiveBrokerSituationDTO(
                new LiveBrokerDTO(new LiveBrokerId(0), "Fake"), empty));
    }

    @Provides @Singleton PhoneNumberVerifiedPreference providePhoneNumberAvailablePreference(
            @ForUser SharedPreferences sharedPreferences)
    {
        return new PhoneNumberVerifiedPreference(sharedPreferences, PREF_SAVED_VERIFIED_PHONE_NUMBERS, new HashSet<String>());
    }

    @Provides @Singleton @ShowCallToActionFragmentPreference BooleanPreference provideShowCallToActionPreference(
            @ForUser SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_SHOW_CALL_TO_ACTION, true);
    }

    @Provides @Singleton @LiveAvailability BooleanPreference provideLiveAvailibilityPreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_LIVE_AVAILABILITY, false);
    }
}
