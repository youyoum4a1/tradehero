package com.tradehero.th.persistence.prefs;

import android.content.SharedPreferences;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.th.models.kyc.KYCForm;
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

    @Provides @Singleton KYCFormPreference provideKYCFormPreference(
            @ForApp ObjectMapper objectMapper,
            @ForUser SharedPreferences sharedPreferences)
    {
        return new KYCFormPreference(objectMapper, sharedPreferences, PREF_SAVED_KYC_FORM, new KYCForm()
        {
        });
    }
}
