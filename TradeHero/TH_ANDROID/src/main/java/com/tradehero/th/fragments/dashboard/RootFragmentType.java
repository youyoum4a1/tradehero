package com.tradehero.th.fragments.dashboard;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.activities.AdminSettingsActivity;
import com.tradehero.th.activities.AlertManagerActivity;
import com.tradehero.th.activities.FriendsInvitationActivity;
import com.tradehero.th.activities.HomeActivity;
import com.tradehero.th.activities.SettingsActivity;
import com.tradehero.th.activities.StoreScreenActivity;
import com.tradehero.th.activities.UpdateCenterActivity;
import com.tradehero.th.fragments.contestcenter.ContestCenterFragment;
import com.tradehero.th.fragments.discovery.DiscoveryMainFragment;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.trending.TrendingMainFragment;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public enum RootFragmentType
{
    // Tab host
    TRENDING(R.layout.tab_indicator_holo,
            R.string.dashboard_trending,
            R.string.dashboard_trending_key,
            R.drawable.icn_menu_trending,
            TrendingMainFragment.class,
            null,
            AnalyticsConstants.TabBar_Trade),
    ME(R.layout.home_selector,
            R.string.dashboard_timeline,
            R.string.dashboard_timeline_key,
            R.drawable.icn_menu_home,
            MeTimelineFragment.class,
            null,
            AnalyticsConstants.TabBar_Me),
    DISCOVERY(R.layout.tab_indicator_holo,
            R.string.discovery,
            R.string.dashboard_discovery_key,
            R.drawable.icn_menu_compass_white,
            DiscoveryMainFragment.class,
            null,
            AnalyticsConstants.TabBar_Discovery),
    COMMUNITY(R.layout.tab_indicator_holo,
            R.string.dashboard_community,
            R.string.dashboard_community_key,
            R.drawable.icn_menu_leaderboards,
            LeaderboardCommunityFragment.class,
            null,
            AnalyticsConstants.TabBar_Community),
    CONTEST_CENTER(R.layout.tab_indicator_holo,
            R.string.dashboard_contest_center,
            R.string.dashboard_contest_center_key,
            R.drawable.icn_menu_contest_center,
            ContestCenterFragment.class,
            null,
            AnalyticsConstants.TabBar_ContestCenter),
    // Side menu
    HOME(R.layout.residemenu_text_item,
            R.string.dashboard_home,
            R.string.dashboard_home_key,
            R.color.transparent,
            null,
            HomeActivity.class,
            AnalyticsConstants.TabBar_Home),
    UPDATE_CENTER(R.layout.residemenu_item_update_center,
            R.string.dashboard_message_center,
            R.string.dashboard_message_center_key,
            R.color.transparent,
            null,
            UpdateCenterActivity.class,
            AnalyticsConstants.TabBar_UpdateCenter),
    ALERTS(R.layout.residemenu_text_item,
            R.string.dashboard_alerts,
            R.string.dashboard_alerts_key,
            R.color.transparent,
            null,
            AlertManagerActivity.class,
            AnalyticsConstants.TabBar_Alerts),
    FRIEND_REFERRAL(R.layout.residemenu_text_item,
            R.string.dashboard_referral,
            R.string.dashboard_referral_key,
            R.color.transparent,
            null,
            FriendsInvitationActivity.class,
            AnalyticsConstants.TabBar_FriendReferral),
    STORE(R.layout.residemenu_text_item,
            R.string.dashboard_store,
            R.string.dashboard_store_key,
            R.color.transparent,
            null,
            StoreScreenActivity.class,
            AnalyticsConstants.TabBar_Store),
    SETTING(R.layout.residemenu_item_settings,
            R.string.dashboard_menu_settings,
            R.string.dashboard_menu_settings_key,
            R.color.transparent,
            null,
            SettingsActivity.class,
            AnalyticsConstants.TabBar_Settings),
    ADMIN_SETTINGS(R.layout.residemenu_text_item,
            R.string.dashboard_admin_settings,
            R.string.dashboard_admin_settings_key,
            R.color.transparent,
            null,
            AdminSettingsActivity.class,
            AnalyticsConstants.TabBar_AdminSettings);

    @LayoutRes private static final int DEFAULT_VIEW_LAYOUT_ID = R.layout.tab_indicator_holo;

    @LayoutRes public final int viewResId;
    @StringRes public final int stringResId;
    @StringRes public final int stringKeyResId;
    @DrawableRes public final int drawableResId;
    @Nullable public final Class<? extends Fragment> fragmentClass;
    @Nullable public final Class<? extends Activity> activityClass;
    public final String analyticsString;

    private RootFragmentType(
            @LayoutRes int viewResId,
            @StringRes int stringResId,
            @StringRes int stringKeyResId,
            @DrawableRes int drawableResId,
            @Nullable Class<? extends Fragment> fragmentClass,
            @Nullable Class<? extends Activity> activityClass,
            String analyticsString)
    {
        this.viewResId = viewResId;
        this.stringResId = stringResId;
        this.stringKeyResId = stringKeyResId;
        this.drawableResId = drawableResId;
        this.fragmentClass = fragmentClass;
        this.activityClass = activityClass;
        this.analyticsString = analyticsString;
    }

    public boolean hasCustomView()
    {
        return viewResId != DEFAULT_VIEW_LAYOUT_ID;
    }

    @NonNull public static Collection<RootFragmentType> forResideMenu()
    {
        List<RootFragmentType> forResideMenu = new ArrayList<>();
        for (RootFragmentType type: values())
        {
            if (type.activityClass != null)
            {
                forResideMenu.add(type);
            }
        }
        if (Constants.RELEASE)
        {
            forResideMenu.remove(ADMIN_SETTINGS);
        }
        return Collections.unmodifiableCollection(forResideMenu);
    }

    @NonNull public static Collection<RootFragmentType> forBottomBar()
    {
        List<RootFragmentType> forBottomBar = new ArrayList<>();
        for (RootFragmentType type: values())
        {
            if (type.fragmentClass != null)
            {
                forBottomBar.add(type);
            }
        }
        return Collections.unmodifiableCollection(forBottomBar);
    }

    @NonNull public static RootFragmentType getInitialTab()
    {
        return RootFragmentType.TRENDING;
    }
}
