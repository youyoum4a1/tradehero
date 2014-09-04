package com.tradehero.th.fragments.settings;

import android.app.Activity;
import android.support.v4.preference.PreferenceFragment;
import com.tradehero.th.R;
import com.tradehero.th.activities.MarketUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SendLoveViewHolder extends OneSettingViewHolder
{
    @NotNull private final MarketUtil marketUtil;

    //<editor-fold desc="Constructors">
    @Inject public SendLoveViewHolder(@NotNull MarketUtil marketUtil)
    {
        super();
        this.marketUtil = marketUtil;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_primary_send_love;
    }

    @Override protected void handlePrefClicked()
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            Activity activity = preferenceFragmentCopy.getActivity();
            if (activity != null)
            {
                marketUtil.showAppOnMarket(activity);
            }
        }
    }
}
