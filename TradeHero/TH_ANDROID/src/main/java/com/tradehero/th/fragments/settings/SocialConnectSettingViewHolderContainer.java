package com.tradehero.th.fragments.settings;

import android.preference.Preference;
import android.preference.PreferenceCategory;
import com.tradehero.th2.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.MarketSegment;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SocialConnectSettingViewHolderContainer implements SettingViewHolder
{
    //@NotNull protected SocialConnectFacebookSettingViewHolder socialConnectFacebookSettingViewHolder;
    @NotNull protected SocialConnectLinkedInSettingViewHolder socialConnectLinkedInSettingViewHolder;
    @NotNull protected SocialConnectQQSettingViewHolder socialConnectQQSettingViewHolder;
    //@NotNull protected SocialConnectTwitterSettingViewHolder socialConnectTwitterSettingViewHolder;
    @NotNull protected SocialConnectWeiboSettingViewHolder socialConnectWeiboSettingViewHolder;

    @Nullable protected PreferenceCategory container;

    //<editor-fold desc="Constructors">
    @Inject public SocialConnectSettingViewHolderContainer(
            //@NotNull SocialConnectFacebookSettingViewHolder socialConnectFacebookSettingViewHolder,
            @NotNull SocialConnectLinkedInSettingViewHolder socialConnectLinkedInSettingViewHolder,
            @NotNull SocialConnectQQSettingViewHolder socialConnectQQSettingViewHolder,
            //@NotNull SocialConnectTwitterSettingViewHolder socialConnectTwitterSettingViewHolder,
            @NotNull SocialConnectWeiboSettingViewHolder socialConnectWeiboSettingViewHolder)
    {
        //this.socialConnectFacebookSettingViewHolder = socialConnectFacebookSettingViewHolder;
        this.socialConnectLinkedInSettingViewHolder = socialConnectLinkedInSettingViewHolder;
        this.socialConnectQQSettingViewHolder = socialConnectQQSettingViewHolder;
        //this.socialConnectTwitterSettingViewHolder = socialConnectTwitterSettingViewHolder;
        this.socialConnectWeiboSettingViewHolder = socialConnectWeiboSettingViewHolder;
    }
    //</editor-fold>

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
        container = (PreferenceCategory) preferenceFragment.findPreference(preferenceFragment.getString(R.string.key_settings_sharing_group));
        //socialConnectFacebookSettingViewHolder.initViews(preferenceFragment);
        socialConnectLinkedInSettingViewHolder.initViews(preferenceFragment);
        socialConnectQQSettingViewHolder.initViews(preferenceFragment);
        //socialConnectTwitterSettingViewHolder.initViews(preferenceFragment);
        socialConnectWeiboSettingViewHolder.initViews(preferenceFragment);

        if (Constants.TAP_STREAM_TYPE.marketSegment.equals(MarketSegment.CHINA))
            // TODO perhaps reordering should do
        {
            //removePref(socialConnectFacebookSettingViewHolder.clickablePref);
            //removePref(socialConnectTwitterSettingViewHolder.clickablePref);
        }
    }

    @Override public void destroyViews()
    {
        socialConnectWeiboSettingViewHolder.destroyViews();
        //socialConnectTwitterSettingViewHolder.destroyViews();
        socialConnectQQSettingViewHolder.destroyViews();
        socialConnectLinkedInSettingViewHolder.destroyViews();
        //socialConnectFacebookSettingViewHolder.destroyViews();
    }

    protected void removePref(@Nullable Preference preference)
    {
        PreferenceCategory containerCopy = container;
        if (containerCopy != null && preference != null)
        {
            containerCopy.removePreference(preference);
        }
    }

    public void changeSharing(SocialNetworkEnum socialNetworkEnum, boolean enable)
    {
        switch (socialNetworkEnum)
        {
            //case FB:
            //    socialConnectFacebookSettingViewHolder.changeStatus(enable);
            //    break;
            case LN:
                socialConnectLinkedInSettingViewHolder.changeStatus(enable);
                break;
            case QQ:
                socialConnectQQSettingViewHolder.changeStatus(enable);
                break;
            //case TW:
            //    socialConnectTwitterSettingViewHolder.changeStatus(enable);
            //    break;
            case WB:
                socialConnectWeiboSettingViewHolder.changeStatus(enable);
                break;
        }
    }
}
