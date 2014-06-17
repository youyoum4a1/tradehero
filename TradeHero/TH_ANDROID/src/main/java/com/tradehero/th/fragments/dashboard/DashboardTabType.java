package com.tradehero.th.fragments.dashboard;

import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.home.HomeFragment;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.portfolio.PortfolioListFragment;
import com.tradehero.th.fragments.settings.AdminSettingsFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.utils.Constants;
import java.util.ArrayList;
import java.util.List;

public enum DashboardTabType
{
    //NEWS(R.string.dashboard_headline, R.drawable.trending_selector, HeadlineFragment.class),
    TIMELINE(R.layout.home_selector, R.string.dashboard_home, R.color.transparent, MeTimelineFragment.class),
    HOME(R.string.home, R.drawable.icn_menu_trending, HomeFragment.class),
    TRENDING(R.string.dashboard_trending, R.drawable.icn_menu_trending, TrendingFragment.class),
    COMMUNITY(R.string.dashboard_community, R.drawable.icn_menu_leaderboards, LeaderboardCommunityFragment.class),
    PORTFOLIO(R.string.dashboard_portfolio, R.drawable.icn_menu_portfolio, PortfolioListFragment.class, false),
    UPDATE_CENTER(R.layout.update_center_selector, R.string.message_center, R.color.transparent, UpdateCenterFragment.class),
    //UPDATE_CENTER(R.string.update_center, R.drawable.icn_menu_messages, UpdateCenterFragment.class),
    REFERRAL(R.string.dashboard_referral, R.drawable.icn_menu_referral, FriendsInvitationFragment.class),
    STORE(R.string.dashboard_store, R.drawable.icn_menu_store, StoreScreenFragment.class),
    SETTING(R.string.settings, R.drawable.icn_menu_settings, SettingsFragment.class),
    ADMIN_SETTINGS(R.string.dashboard_admin_settings, R.drawable.icn_menu_settings, AdminSettingsFragment.class);

    private static final int DEFAULT_VIEW_LAYOUT_ID = R.layout.tab_indicator_holo;

    public final int viewResId;
    public final int stringResId;
    public final int drawableResId;
    public final boolean show;

    public final Class<? extends Fragment> fragmentClass;

    private DashboardTabType(
            int viewResId,
            int stringResId,
            int drawableResId,
            Class<? extends Fragment> fragmentClass,
            boolean show)
    {
        this.viewResId = viewResId;
        this.stringResId = stringResId;
        this.drawableResId = drawableResId;
        this.show = show;
        this.fragmentClass = fragmentClass;
    }

    private DashboardTabType(
            int viewResId,
            int stringResId,
            int drawableResId,
            Class<? extends Fragment> fragmentClass)
    {
        this(viewResId, stringResId, drawableResId, fragmentClass, true);
    }

    private DashboardTabType(int stringResId, int drawableResId, Class<? extends Fragment> fragmentClass, boolean show)
    {
        this(DEFAULT_VIEW_LAYOUT_ID, stringResId, drawableResId, fragmentClass, show);
    }

    private DashboardTabType(int stringResId, int drawableResId, Class<? extends Fragment> fragmentClass)
    {
        this(DEFAULT_VIEW_LAYOUT_ID, stringResId, drawableResId, fragmentClass, true);
    }
    
    public boolean hasCustomView()
    {
        return viewResId != DEFAULT_VIEW_LAYOUT_ID;
    }

    public static List<DashboardTabType> usableValues()
    {
        List<DashboardTabType> values = new ArrayList<>();
        for (DashboardTabType value : values())
        {
            if (!(value.equals(DashboardTabType.ADMIN_SETTINGS) && Constants.RELEASE))
            {
                values.add(value);
            }
        }
        return values;
    }
}
