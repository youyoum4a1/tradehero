package com.tradehero.th.fragments.security;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionFragment;
import com.tradehero.th.fragments.news.NewsHeadlineFragment;
import com.tradehero.th.fragments.position.SecurityPositionListFragment;
import com.tradehero.th.models.chart.ChartTimeSpan;
import timber.log.Timber;

public class BuySellBottomStockPagerAdapter extends FragmentPagerAdapter
{
    public static final int FRAGMENT_ID_CHART = 0;
    public static final int FRAGMENT_ID_DISCUSS = 1;
    public static final int FRAGMENT_ID_NEWS = 2;
    public static final int FRAGMENT_ID_HISTORY = 3;

    @NonNull private final Context context;
    @NonNull private final OwnedPortfolioId applicablePortfolioId;
    @NonNull private final SecurityId securityId;
    @NonNull private final UserBaseKey shownUser;

    //<editor-fold desc="Constructors">
    public BuySellBottomStockPagerAdapter(
            @NonNull Context context,
            @NonNull FragmentManager fragmentManager,
            @NonNull OwnedPortfolioId applicablePortfolioId,
            @NonNull SecurityId securityId,
            @NonNull UserBaseKey shownUser)
    {
        super(fragmentManager);
        this.context = context;
        this.applicablePortfolioId = applicablePortfolioId;
        this.securityId = securityId;
        this.shownUser = shownUser;
    }
    //</editor-fold>

    public static ChartTimeSpan getDefaultChartTimeSpan()
    {
        return new ChartTimeSpan(ChartTimeSpan.MONTH_3);
    }

    @Override public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case FRAGMENT_ID_CHART:
                return context.getString(R.string.security_info);
            case FRAGMENT_ID_DISCUSS:
                return context.getString(R.string.security_discussions);
            case FRAGMENT_ID_NEWS:
                return context.getString(R.string.security_news);
            case FRAGMENT_ID_HISTORY:
                return context.getString(R.string.security_history);
        }
        return super.getPageTitle(position);
    }

    @Override public int getCount()
    {
        return 4;
    }

    @Override public Fragment getItem(int position)
    {
        Fragment fragment;
        Bundle args = new Bundle();

        switch (position)
        {
            case FRAGMENT_ID_CHART:
                fragment = new ChartFragment();
                ChartFragment.putSecurityId(args, securityId);
                args.putInt(ChartFragment.BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, View.VISIBLE);
                args.putLong(ChartFragment.BUNDLE_KEY_TIME_SPAN_SECONDS_LONG, getDefaultChartTimeSpan().duration);
                break;
            case FRAGMENT_ID_DISCUSS:
                fragment = new SecurityDiscussionFragment();
                SecurityDiscussionFragment.setHasOptionMenu(args, false);
                SecurityDiscussionFragment.putSecurityId(args, securityId);
                break;
            case FRAGMENT_ID_NEWS:
                fragment = new NewsHeadlineFragment();
                NewsHeadlineFragment.putSecurityId(args, securityId);
                break;
            case FRAGMENT_ID_HISTORY:
                fragment = new SecurityPositionListFragment();
                SecurityPositionListFragment.setHasOptionMenu(args, false);
                SecurityPositionListFragment.putShownUser(args, shownUser);
                SecurityPositionListFragment.putSecurityId(args, securityId);
                SecurityPositionListFragment.putApplicablePortfolioId(args, applicablePortfolioId);
                break;

            default:
                Timber.w("Not supported index " + position);
                throw new UnsupportedOperationException("Not implemented");
        }

        fragment.setArguments(args);
        fragment.setRetainInstance(false);
        return fragment;
    }
}
