package com.tradehero.th.persistence.prefs;

import android.content.SharedPreferences;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.models.kyc.EmptyKYCForm;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        complete = false,
        library = true
)
public class PreferenceGameLiveModule
{
    private static final String PREF_SAVED_KYC_FORM = "PREF_SAVED_KYC_FORM";
    private static final String PREF_SHOW_CALL_TO_ACTION = "PREF_SHOW_CALL_TO_ACTION";

    @Provides @Singleton KYCFormPreference provideKYCFormPreference(
            @ForApp ObjectMapper objectMapper,
            @ForUser SharedPreferences sharedPreferences)
    {
        return new KYCFormPreference(objectMapper, sharedPreferences, PREF_SAVED_KYC_FORM, new EmptyKYCForm());
    }

    @Provides @Singleton @ShowCallToActionFragmentPreference BooleanPreference provideShowCallToActionPreference(@ForUser SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_SHOW_CALL_TO_ACTION, true);
    }
}
