package com.tradehero.th.fragments.settings;

import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.persistence.prefs.IsVisitedReferralCodeSettings;
import javax.inject.Inject;
import android.support.annotation.NonNull;

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
