package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.prefs.IsVisitedReferralCodeSettings;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class ReferralCodeUnreadPreference extends BaseShowUnreadPreference
{
    @Inject @IsVisitedReferralCodeSettings BooleanPreference mIsVisitedSettingsPreference;

    //<editor-fold desc="Constructors">
    public ReferralCodeUnreadPreference(@NotNull Context context, @NotNull AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(context, this);
    }
    //</editor-fold>

    public boolean isVisited()
    {
        return mIsVisitedSettingsPreference.get();
    }

    @Override public void setVisited(boolean visited)
    {
        mIsVisitedSettingsPreference.set(true);
    }
}
