package com.tradehero.th.fragments.settings;

import com.tradehero.th.R;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import javax.inject.Inject;

public class TopBannerSettingViewHolder extends OneSettingViewHolder
{
    //<editor-fold desc="Constructors">
    @Inject public TopBannerSettingViewHolder()
    {
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_preference_top_banner;
    }

    @Override protected void handlePrefClicked()
    {
        DashboardPreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            DashboardNavigator navigator = preferenceFragmentCopy.getNavigator();
            if (navigator != null)
            {
                navigator.pushFragment(FriendsInvitationFragment.class, null, null);
            }
        }
    }
}
