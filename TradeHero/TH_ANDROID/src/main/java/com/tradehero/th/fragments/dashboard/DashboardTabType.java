package com.tradehero.th.fragments.dashboard;

import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;

public enum DashboardTabType
{
    TRENDING(R.layout.tab_indicator_holo,
            R.string.dashboard_trending, R.string.dashboard_trending_key,
            R.drawable.launcher, TrendingFragment.class),
    UPDATE_CENTER(R.layout.update_center_selector,
            R.string.dashboard_message_center, R.string.dashboard_message_center_key,
            R.color.transparent, UpdateCenterFragment.class),
    REFERRAL(R.layout.tab_indicator_holo,
            R.string.dashboard_referral, R.string.dashboard_referral_key,
            R.drawable.launcher, FriendsInvitationFragment.class);

    private static final int DEFAULT_VIEW_LAYOUT_ID = R.layout.tab_indicator_holo;

    public final int viewResId;
    public final int stringResId;
    public final int stringKeyResId;
    public final int drawableResId;
    public final boolean show;
    public final Class<? extends Fragment> fragmentClass;

    private DashboardTabType(
            int viewResId,
            int stringResId,
            int stringKeyResId,
            int drawableResId,
            Class<? extends Fragment> fragmentClass)
    {
        this(viewResId, stringResId, stringKeyResId, drawableResId, fragmentClass, true);
    }

    private DashboardTabType(
            int viewResId,
            int stringResId,
            int stringKeyResId,
            int drawableResId,
            Class<? extends Fragment> fragmentClass,
            boolean show)
    {
        this.viewResId = viewResId;
        this.stringResId = stringResId;
        this.stringKeyResId = stringKeyResId;
        this.drawableResId = drawableResId;
        this.show = show;
        this.fragmentClass = fragmentClass;
    }

}
