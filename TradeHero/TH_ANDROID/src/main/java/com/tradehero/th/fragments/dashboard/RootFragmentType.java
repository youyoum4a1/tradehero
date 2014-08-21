package com.tradehero.th.fragments.dashboard;

import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.contestcenter.ContestCenterFragment;
import com.tradehero.th.fragments.discovery.DiscoveryMainFragment;
import com.tradehero.th.fragments.home.HomeFragment;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.settings.AdminSettingsFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
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
    HOME(R.layout.tab_indicator_holo,
            R.string.dashboard_home, R.string.dashboard_home_key,
            R.drawable.icn_menu_home, HomeFragment.class),
    TRENDING(R.layout.tab_indicator_holo,
            R.string.dashboard_trending, R.string.dashboard_trending_key,
            R.drawable.icn_menu_trending, TrendingFragment.class),
    COMMUNITY(R.layout.tab_indicator_holo,
            R.string.dashboard_community, R.string.dashboard_community_key,
            R.drawable.icn_menu_leaderboards, LeaderboardCommunityFragment.class),
    UPDATE_CENTER(R.layout.update_center_selector,
            R.string.dashboard_message_center, R.string.dashboard_message_center_key,
            R.color.transparent, UpdateCenterFragment.class),
    REFERRAL(R.layout.tab_indicator_holo,
            R.string.dashboard_referral, R.string.dashboard_referral_key,
            R.drawable.icn_menu_referral, FriendsInvitationFragment.class),
    CONTEST_CENTER(R.layout.tab_indicator_holo,
            R.string.dashboard_contest_center, R.string.dashboard_contest_center_key,
            R.drawable.icn_menu_contest_center, ContestCenterFragment.class),
    STORE(R.layout.tab_indicator_holo,
            R.string.dashboard_store, R.string.dashboard_store_key,
            R.drawable.icn_menu_store, StoreScreenFragment.class),
    SETTING(R.layout.tab_indicator_holo,
            R.string.dashboard_menu_settings, R.string.dashboard_menu_settings_key,
            R.drawable.icn_menu_settings, SettingsFragment.class),
    ADMIN_SETTINGS(R.layout.tab_indicator_holo,
            R.string.dashboard_admin_settings, R.string.dashboard_admin_settings_key,
            R.drawable.icn_menu_settings, AdminSettingsFragment.class),
    TEMP(R.layout.tab_indicator_holo,
            R.string.security_news, R.string.security_news,
            R.drawable.icn_menu_settings, DiscoveryMainFragment.class),
    ;

    private static final int DEFAULT_VIEW_LAYOUT_ID = R.layout.tab_indicator_holo;

    public final int viewResId;
    public final int stringResId;
    public final int stringKeyResId;
    public final int drawableResId;
    public final Class<? extends Fragment> fragmentClass;

    private RootFragmentType(
            int viewResId,
            int stringResId,
            int stringKeyResId,
            int drawableResId,
            Class<? extends Fragment> fragmentClass)
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
                TIMELINE, HOME, TRENDING, COMMUNITY, UPDATE_CENTER, REFERRAL, CONTEST_CENTER, STORE, SETTING
        ));
        addAdminMenuIfNeeded(forResideMenu);
        return Collections.unmodifiableCollection(forResideMenu);
    }

    public static Collection<RootFragmentType> forBottomBar()
    {
        List<RootFragmentType> forBottomBar = Arrays.asList(
                TIMELINE, TRENDING, UPDATE_CENTER, CONTEST_CENTER
        );
        return Collections.unmodifiableCollection(forBottomBar);
    }

    private static void addAdminMenuIfNeeded(List<RootFragmentType> forResideMenu)
    {
        if (!Constants.RELEASE) {
            forResideMenu.add(ADMIN_SETTINGS);
        }
    }
}
