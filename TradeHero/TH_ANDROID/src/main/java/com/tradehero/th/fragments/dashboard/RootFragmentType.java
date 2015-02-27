package com.tradehero.th.fragments.dashboard;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.contestcenter.ContestCenterFragment;
import com.tradehero.th.fragments.discovery.DiscoveryMainFragment;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.settings.AdminSettingsFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.settings.SettingsReferralCodeFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.trending.TrendingMainFragment;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public enum RootFragmentType
{
    ME(R.layout.home_selector,
            R.string.dashboard_timeline, R.string.dashboard_timeline_key,
            R.drawable.icn_menu_home, MeTimelineFragment.class, AnalyticsConstants.TabBar_Me),
    TRENDING(R.layout.tab_indicator_holo,
            R.string.dashboard_trending, R.string.dashboard_trending_key,
            R.drawable.icn_menu_trending, TrendingMainFragment.class, AnalyticsConstants.TabBar_Trade),
    DISCOVERY(R.layout.tab_indicator_holo,
            R.string.discovery, R.string.dashboard_discovery_key,
            R.drawable.icn_menu_compass_white, DiscoveryMainFragment.class, AnalyticsConstants.TabBar_Discovery),
    COMMUNITY(R.layout.tab_indicator_holo,
            R.string.dashboard_community, R.string.dashboard_community_key,
            R.drawable.icn_menu_leaderboards, LeaderboardCommunityFragment.class, AnalyticsConstants.TabBar_Community),
    CONTEST_CENTER(R.layout.tab_indicator_holo,
            R.string.dashboard_contest_center, R.string.dashboard_contest_center_key,
            R.drawable.icn_menu_contest_center, ContestCenterFragment.class, AnalyticsConstants.TabBar_ContestCenter),
    TIMELINE(R.layout.home_selector,
            R.string.dashboard_timeline, R.string.dashboard_timeline_key,
            R.color.transparent, MeTimelineFragment.class, AnalyticsConstants.TabBar_Me),
    STORE(R.layout.residemenu_item_store,
            R.string.dashboard_store, R.string.dashboard_store_key,
            R.drawable.icn_menu_store, StoreScreenFragment.class, AnalyticsConstants.TabBar_Store),
    FRIEND_REFERRAL(R.layout.residemenu_item_refererral,
            R.string.dashboard_referral, R.string.dashboard_referral_key,
            R.drawable.icn_menu_referral, SettingsReferralCodeFragment.class, AnalyticsConstants.TabBar_FriendReferral),
    SETTING(R.layout.residemenu_item_settings,
            R.string.dashboard_menu_settings, R.string.dashboard_menu_settings_key,
            R.drawable.icn_menu_settings, SettingsFragment.class, AnalyticsConstants.TabBar_Settings),
    ADMIN_SETTINGS(R.layout.tab_indicator_holo,
            R.string.dashboard_admin_settings, R.string.dashboard_admin_settings_key,
            R.drawable.icn_menu_settings, AdminSettingsFragment.class, AnalyticsConstants.TabBar_AdminSettings),
    DIVIDER(R.layout.residemenu_item_divider,
            R.string.dashboard_divider, R.string.dashboard_divider_key,
            R.drawable.icn_menu_settings, null, AnalyticsConstants.TabBar_Divider);

    @LayoutRes private static final int DEFAULT_VIEW_LAYOUT_ID = R.layout.tab_indicator_holo;

    @LayoutRes public final int viewResId;
    @StringRes public final int stringResId;
    @StringRes public final int stringKeyResId;
    @DrawableRes public final int drawableResId;
    @Nullable public final Class<? extends Fragment> fragmentClass;
    public final String analyticsString;

    private RootFragmentType(
            @LayoutRes int viewResId,
            @StringRes int stringResId,
            @StringRes int stringKeyResId,
            @DrawableRes int drawableResId,
            @Nullable Class<? extends Fragment> fragmentClass,
            String analyticsString)
    {
        this.viewResId = viewResId;
        this.stringResId = stringResId;
        this.stringKeyResId = stringKeyResId;
        this.drawableResId = drawableResId;
        this.fragmentClass = fragmentClass;
        this.analyticsString = analyticsString;
    }

    public boolean hasCustomView()
    {
        return viewResId != DEFAULT_VIEW_LAYOUT_ID;
    }

    @NonNull public static Collection<RootFragmentType> forResideMenu()
    {
        List<RootFragmentType> forResideMenu = new ArrayList<>(Arrays.asList(
                TIMELINE, DIVIDER, TRENDING, DISCOVERY, COMMUNITY, CONTEST_CENTER, DIVIDER, FRIEND_REFERRAL, STORE, SETTING
        ));
        addAdminMenuIfNeeded(forResideMenu);
        return Collections.unmodifiableCollection(forResideMenu);
    }

    @NonNull public static Collection<RootFragmentType> forBottomBar()
    {
        List<RootFragmentType> forBottomBar = Arrays.asList(
                TRENDING, ME, DISCOVERY, COMMUNITY, CONTEST_CENTER
        );
        return Collections.unmodifiableCollection(forBottomBar);
    }

    private static void addAdminMenuIfNeeded(@NonNull List<RootFragmentType> forResideMenu)
    {
        if (!Constants.RELEASE)
        {
            forResideMenu.add(ADMIN_SETTINGS);
        }
    }

    @NonNull public static RootFragmentType getInitialTab()
    {
        return RootFragmentType.ME;
    }
}
