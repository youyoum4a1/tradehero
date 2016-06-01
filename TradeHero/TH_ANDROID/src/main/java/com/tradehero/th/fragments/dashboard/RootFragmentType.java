package com.ayondo.academy.fragments.dashboard;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.ayondo.academy.R;
import com.ayondo.academy.activities.AdminSettingsActivity;
import com.ayondo.academy.activities.AlertManagerActivity;
import com.ayondo.academy.activities.FriendsInvitationActivity;
import com.ayondo.academy.activities.SettingsActivity;
import com.ayondo.academy.activities.StoreScreenActivity;
import com.ayondo.academy.activities.UpdateCenterActivity;
import com.ayondo.academy.fragments.contestcenter.ContestCenterFragment;
import com.ayondo.academy.fragments.discovery.DiscoveryMainFragment;
import com.ayondo.academy.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.ayondo.academy.fragments.timeline.MeTimelineFragment;
import com.ayondo.academy.fragments.trending.TrendingMainFragment;
import com.ayondo.academy.utils.Constants;
import com.ayondo.academy.utils.metrics.AnalyticsConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public enum RootFragmentType
{
    // Tab host
    TRENDING(R.layout.left_drawer_item,
            R.string.dashboard_trending,
            R.string.dashboard_trending_key,
            R.drawable.icn_menu_trending,
            TrendingMainFragment.class,
            null,
            AnalyticsConstants.TabBar_Trade),
    COMMUNITY(R.layout.left_drawer_item,
            R.string.dashboard_community,
            R.string.dashboard_community_key,
            R.drawable.icn_menu_leaderboards,
            LeaderboardCommunityFragment.class,
            null,
            AnalyticsConstants.TabBar_Community),
    DISCOVERY(R.layout.left_drawer_item,
            R.string.discovery,
            R.string.dashboard_discovery_key,
            R.drawable.icn_menu_compass_white,
            DiscoveryMainFragment.class,
            null,
            AnalyticsConstants.TabBar_Discovery),
    CONTEST_CENTER(R.layout.left_drawer_item,
            R.string.dashboard_contest_center,
            R.string.dashboard_contest_center_key,
            R.drawable.icn_menu_contest_center,
            ContestCenterFragment.class,
            null,
            AnalyticsConstants.TabBar_ContestCenter),
    ME(R.layout.left_drawer_item,
            R.string.dashboard_timeline,
            R.string.dashboard_timeline_key,
            R.drawable.icn_menu_home,
            MeTimelineFragment.class,
            null,
            AnalyticsConstants.TabBar_Me),
    // Side menu
    UPDATE_CENTER(R.layout.left_drawer_item_update_center,
            R.string.dashboard_message_center,
            R.string.dashboard_message_center_key,
            R.drawable.icn_side_menu_inbox,
            null,
            UpdateCenterActivity.class,
            AnalyticsConstants.TabBar_UpdateCenter),
    ALERTS(R.layout.left_drawer_item,
            R.string.dashboard_alerts,
            R.string.dashboard_alerts_key,
            R.drawable.icn_side_menu_alerts,
            null,
            AlertManagerActivity.class,
            AnalyticsConstants.TabBar_Alerts),
    FRIEND_REFERRAL(R.layout.left_drawer_item,
            R.string.dashboard_referral,
            R.string.dashboard_referral_key,
            R.drawable.icn_side_menu_refer,
            null,
            FriendsInvitationActivity.class,
            AnalyticsConstants.TabBar_FriendReferral),
    STORE(R.layout.left_drawer_item,
            R.string.dashboard_store,
            R.string.dashboard_store_key,
            R.drawable.icn_side_menu_store,
            null,
            StoreScreenActivity.class,
            AnalyticsConstants.TabBar_Store),
    SETTING(R.layout.left_drawer_item_settings,
            R.string.dashboard_menu_settings,
            R.string.dashboard_menu_settings_key,
            R.drawable.icn_side_menu_settings,
            null,
            SettingsActivity.class,
            AnalyticsConstants.TabBar_Settings),
    ADMIN_SETTINGS(R.layout.left_drawer_item,
            R.string.dashboard_admin_settings,
            R.string.dashboard_admin_settings_key,
            R.drawable.icn_side_menu_settings,
            null,
            AdminSettingsActivity.class,
            AnalyticsConstants.TabBar_AdminSettings);

    @LayoutRes private static final int DEFAULT_VIEW_LAYOUT_ID = R.layout.left_drawer_item;

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

    @NonNull public static Collection<RootFragmentType> forLeftDrawer()
    {
        List<RootFragmentType> forLeftDrawer = new ArrayList<>();
        for (RootFragmentType type : values())
        {
            if (type.activityClass != null)
            {
                forLeftDrawer.add(type);
            }
        }
        if (Constants.RELEASE)
        {
            forLeftDrawer.remove(ADMIN_SETTINGS);
        }
        return Collections.unmodifiableCollection(forLeftDrawer);
    }

    @NonNull public static Collection<RootFragmentType> forBottomBar()
    {
        List<RootFragmentType> forBottomBar = new ArrayList<>();
        for (RootFragmentType type : values())
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
