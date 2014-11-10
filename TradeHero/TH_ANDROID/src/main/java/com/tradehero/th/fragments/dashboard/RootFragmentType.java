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
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.utils.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public enum RootFragmentType
{
    TIMELINE(R.layout.home_selector,
            R.string.dashboard_timeline, R.string.dashboard_timeline_key,
            R.color.transparent, MeTimelineFragment.class),
    ME(R.layout.home_selector,
            R.string.dashboard_timeline, R.string.dashboard_timeline_key,
            R.drawable.icn_menu_home, MeTimelineFragment.class),
    TRENDING(R.layout.tab_indicator_holo,
            R.string.dashboard_trending, R.string.dashboard_trending_key,
            R.drawable.icn_menu_trending, TrendingFragment.class),
    COMMUNITY(R.layout.tab_indicator_holo,
            R.string.dashboard_community, R.string.dashboard_community_key,
            R.drawable.icn_menu_leaderboards, LeaderboardCommunityFragment.class),
    CONTEST_CENTER(R.layout.tab_indicator_holo,
            R.string.dashboard_contest_center, R.string.dashboard_contest_center_key,
            R.drawable.icn_menu_contest_center, ContestCenterFragment.class),
    STORE(R.layout.residemenu_item_store,
            R.string.dashboard_store, R.string.dashboard_store_key,
            R.drawable.icn_menu_store, StoreScreenFragment.class),
    SETTING(R.layout.residemenu_item_settings,
            R.string.dashboard_menu_settings, R.string.dashboard_menu_settings_key,
            R.drawable.icn_menu_settings, SettingsFragment.class),
    ADMIN_SETTINGS(R.layout.tab_indicator_holo,
            R.string.dashboard_admin_settings, R.string.dashboard_admin_settings_key,
            R.drawable.icn_menu_settings, AdminSettingsFragment.class),
    DISCOVERY(R.layout.tab_indicator_holo,
            R.string.discovery, R.string.dashboard_discovery_key,
            R.drawable.icn_menu_compass_white, DiscoveryMainFragment.class),
    DIVIDER(R.layout.residemenu_item_divider,
            R.string.dashboard_divider, R.string.dashboard_divider_key,
            R.drawable.icn_menu_settings, null);

    @LayoutRes private static final int DEFAULT_VIEW_LAYOUT_ID = R.layout.tab_indicator_holo;

    @LayoutRes public final int viewResId;
    @StringRes public final int stringResId;
    @StringRes public final int stringKeyResId;
    @DrawableRes public final int drawableResId;
    @Nullable public final Class<? extends Fragment> fragmentClass;

    private RootFragmentType(
            @LayoutRes int viewResId,
            @StringRes int stringResId,
            @StringRes int stringKeyResId,
            @DrawableRes int drawableResId,
            @Nullable Class<? extends Fragment> fragmentClass)
    {
        this.viewResId = viewResId;
        this.stringResId = stringResId;
        this.stringKeyResId = stringKeyResId;
        this.drawableResId = drawableResId;
        this.fragmentClass = fragmentClass;
    }

    public boolean hasCustomView()
    {
        return viewResId != DEFAULT_VIEW_LAYOUT_ID;
    }

    public static Collection<RootFragmentType> forResideMenu()
    {
        List<RootFragmentType> forResideMenu = new ArrayList<>(Arrays.asList(
                TIMELINE, DIVIDER, TRENDING, DISCOVERY, COMMUNITY, CONTEST_CENTER, DIVIDER, STORE, SETTING
        ));
        addAdminMenuIfNeeded(forResideMenu);
        return Collections.unmodifiableCollection(forResideMenu);
    }

    public static Collection<RootFragmentType> forBottomBar()
    {
        List<RootFragmentType> forBottomBar = Arrays.asList(
                ME, TRENDING, DISCOVERY, COMMUNITY, CONTEST_CENTER
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
