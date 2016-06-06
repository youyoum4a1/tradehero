package com.androidth.general.fragments.security;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.androidth.general.R;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.fragments.discussion.stock.SecurityDiscussionFragment;
import com.androidth.general.fragments.news.SecurityNewsfeedFragment;
import com.androidth.general.fragments.position.SecurityPositionListFragment;
import com.androidth.general.models.chart.ChartTimeSpan;
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

    @NonNull private final Fragment[] fragments;

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
        fragments = new Fragment[getCount()];
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
                ChartFragment.putButtonSetVisibility(args, View.VISIBLE);
                ChartFragment.putChartTimeSpan(args, getDefaultChartTimeSpan());
                break;
            case FRAGMENT_ID_DISCUSS:
                fragment = new SecurityDiscussionFragment();
                SecurityDiscussionFragment.setHasOptionMenu(args, false);
                SecurityDiscussionFragment.putSecurityId(args, securityId);
                break;
            case FRAGMENT_ID_NEWS:
                fragment = new SecurityNewsfeedFragment();
                SecurityNewsfeedFragment.putSecurityId(args, securityId);
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

        fragments[position] = fragment;
        fragment.setArguments(args);
        fragment.setRetainInstance(false);
        return fragment;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object)
    {
        fragments[position] = null;
        super.destroyItem(container, position, object);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        for (Fragment fragment : fragments)
        {
            if (fragment != null)
            {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
