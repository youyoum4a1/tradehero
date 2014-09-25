package com.tradehero.th.fragments.settings;

import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.persistence.prefs.IsVisitedReferralCodeSettings;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class UnreadSettingPreferenceHolder
{
    @NotNull private final BooleanPreference isVisitedReferralCodeSettingsPreference;

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
