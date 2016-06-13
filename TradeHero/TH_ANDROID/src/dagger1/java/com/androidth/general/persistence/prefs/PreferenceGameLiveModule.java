package com.androidth.general.persistence.prefs;

import android.content.SharedPreferences;

import com.androidth.general.api.live.LiveBrokerKnowledge;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.androidth.general.common.annotation.ForApp;
import com.androidth.general.common.annotation.ForUser;
import com.androidth.general.common.persistence.prefs.BooleanPreference;
import com.androidth.general.api.kyc.EmptyKYCForm;
import com.androidth.general.api.kyc.KYCForm;
import com.androidth.general.api.kyc.StepStatus;
import com.androidth.general.api.live.LiveBrokerDTO;
import com.androidth.general.api.live.LiveBrokerId;
import com.androidth.general.api.live.LiveBrokerSituationDTO;
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
                new LiveBrokerDTO(new LiveBrokerId(LiveBrokerKnowledge.BROKER_ID_AYONDO), "fake"), empty));
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
