package com.androidth.general.fragments.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.androidth.general.common.persistence.prefs.BooleanPreference;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.persistence.prefs.IsVisitedReferralCodeSettings;
import javax.inject.Inject;

public class ReferralCodeUnreadPreference extends BaseShowUnreadPreference
{
    @Inject @IsVisitedReferralCodeSettings BooleanPreference mIsVisitedSettingsPreference;

    //<editor-fold desc="Constructors">
    public ReferralCodeUnreadPreference(@NonNull Context context, @NonNull AttributeSet attrs)
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
