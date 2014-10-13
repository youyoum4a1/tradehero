package com.tradehero.th.fragments.settings;

import android.preference.Preference;
import android.preference.PreferenceCategory;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SocialConnectSettingViewHolderContainer implements SettingViewHolder
{
    private final List<SocialConnectSettingViewHolder> settingViewHolders;

    @Nullable protected PreferenceCategory container;

    //<editor-fold desc="Constructors">
    @Inject public SocialConnectSettingViewHolderContainer(
            @NotNull SocialConnectFacebookSettingViewHolder socialConnectFacebookSettingViewHolder,
            @NotNull SocialConnectLinkedInSettingViewHolder socialConnectLinkedInSettingViewHolder,
            @NotNull SocialConnectQQSettingViewHolder socialConnectQQSettingViewHolder,
            @NotNull SocialConnectTwitterSettingViewHolder socialConnectTwitterSettingViewHolder,
            @NotNull SocialConnectWeiboSettingViewHolder socialConnectWeiboSettingViewHolder)
    {
        settingViewHolders = Arrays.asList(
                socialConnectFacebookSettingViewHolder,
                socialConnectLinkedInSettingViewHolder,
                socialConnectQQSettingViewHolder,
                socialConnectTwitterSettingViewHolder,
                socialConnectWeiboSettingViewHolder);
    }
    //</editor-fold>

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
        container = (PreferenceCategory) preferenceFragment.findPreference(preferenceFragment.getString(R.string.key_settings_sharing_group));
        for (SocialConnectSettingViewHolder settingViewHolder : settingViewHolders)
        {
            settingViewHolder.initViews(preferenceFragment);
        }
    }

    @Override public void destroyViews()
    {
        for (SocialConnectSettingViewHolder settingViewHolder: settingViewHolders)
        {
            settingViewHolder.destroyViews();
        }
    }

    @Override public boolean isUnread()
    {
        boolean isUnread = false;
        for (SocialConnectSettingViewHolder settingViewHolder: settingViewHolders)
        {
            isUnread |= settingViewHolder.isUnread();
        }
        return isUnread;
    }

    @Override public Preference getPreference()
    {
        return container;
    }

    public void changeSharing(SocialNetworkEnum socialNetworkToConnectTo, boolean enable)
    {
        for (SocialConnectSettingViewHolder settingViewHolder: settingViewHolders)
        {
            if (settingViewHolder.getSocialNetworkEnum() == socialNetworkToConnectTo)
            {
                settingViewHolder.changeStatus(enable);
                break;
            }
        }
    }
}
