package com.androidth.general.fragments.settings;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.prefs.BooleanPreference;
import com.androidth.general.persistence.prefs.IsVisitedReferralCodeSettings;
import javax.inject.Inject;

public class UnreadSettingPreferenceHolder
{
    @NonNull private final BooleanPreference isVisitedReferralCodeSettingsPreference;

    //<editor-fold desc="Constructors">
    @Inject public UnreadSettingPreferenceHolder(
            @IsVisitedReferralCodeSettings BooleanPreference isVisitedReferralCodeSettingsPreference)
    {
        super();
        this.isVisitedReferralCodeSettingsPreference = isVisitedReferralCodeSettingsPreference;
    }
    //</editor-fold>

    public boolean hasUnread()
    {
        return !isVisitedReferralCodeSettingsPreference.get(); // || !isVisitedOther.get()
    }
}
