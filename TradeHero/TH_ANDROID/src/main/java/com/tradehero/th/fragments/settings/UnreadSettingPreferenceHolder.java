package com.ayondo.academy.fragments.settings;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.ayondo.academy.persistence.prefs.IsVisitedReferralCodeSettings;
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
