package com.androidth.general.fragments.leaderboard.filter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.androidth.general.R;
import com.androidth.general.api.leaderboard.LeaderboardDTO;
import com.androidth.general.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import com.androidth.general.fragments.base.DashboardFragment;
import com.androidth.general.persistence.leaderboard.LeaderboardCacheRx;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LeaderboardFilterFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_PER_PAGED_FILTERED_LEADERBOARD_KEY_BUNDLE =
            LeaderboardFilterFragment.class.getName() + ".perPagedFilteredLeaderboardKey";

    //TODO Change Analytics
    //@Inject Analytics analytics;
    @Inject LeaderboardCacheRx leaderboardCache;
    @BindView(R.id.leaderboard_filter_slider_container) LeaderboardFilterSliderContainer filterSliderContainer;

    @NonNull protected PerPagedFilteredLeaderboardKey perPagedFilteredLeaderboardKey;
    @Nullable protected LeaderboardDTO leaderboardDTO;

    public static void putPerPagedFilteredLeaderboardKey(@NonNull Bundle args, @NonNull PerPagedFilteredLeaderboardKey key)
    {
        args.putBundle(BUNDLE_KEY_PER_PAGED_FILTERED_LEADERBOARD_KEY_BUNDLE, key.getArgs());
    }

    @NonNull
    private static PerPagedFilteredLeaderboardKey getPerPagedFilteredLeaderboardKey(@NonNull Bundle args)
    {
        return new PerPagedFilteredLeaderboardKey(args.getBundle(BUNDLE_KEY_PER_PAGED_FILTERED_LEADERBOARD_KEY_BUNDLE), null);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            this.perPagedFilteredLeaderboardKey = getPerPagedFilteredLeaderboardKey(savedInstanceState);
        }
        else
        {
            this.perPagedFilteredLeaderboardKey = getPerPagedFilteredLeaderboardKey(getArguments());
        }
        leaderboardDTO = leaderboardCache.getCachedValue(perPagedFilteredLeaderboardKey);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_leaderboard_filter, container, false);
        ButterKnife.bind(this, view);
        initViews(view);
        return view;
    }

    protected void initViews(View view)
    {
        displayPerPagedFilteredLeaderboardKey();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.leaderboard_filter_menu, menu);
        setActionBarTitle(R.string.leaderboard_filter_menu_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                //TODO Change Analytics
                //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_FilterReset));
                break;

            case R.id.btn_leaderboard_filter_confirm:
                //TODO Change Analytics
                //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_FilterDone));
                collectPagedFilteredLeaderboardKey();
                navigator.get().popFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onResume()
    {
        super.onResume();

        //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_FilterShow));
    }

    @Override public void onPause()
    {
        collectPagedFilteredLeaderboardKey();
        super.onPause();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        putPerPagedFilteredLeaderboardKey(outState, this.perPagedFilteredLeaderboardKey);
    }

    public PerPagedFilteredLeaderboardKey getPerPagedFilteredLeaderboardKey()
    {
        collectPagedFilteredLeaderboardKey();
        return perPagedFilteredLeaderboardKey;
    }

    public void collectPagedFilteredLeaderboardKey()
    {
        if (filterSliderContainer != null)
        {
            this.perPagedFilteredLeaderboardKey = filterSliderContainer.getFilteredLeaderboardKey();
        }
    }

    public void linkWith(PerPagedFilteredLeaderboardKey perPagedFilteredLeaderboardKey, boolean andDisplay)
    {
        this.perPagedFilteredLeaderboardKey = perPagedFilteredLeaderboardKey;
        if (andDisplay)
        {
            displayPerPagedFilteredLeaderboardKey();
        }
    }

    public void displayPerPagedFilteredLeaderboardKey()
    {
        if (filterSliderContainer != null)
        {
            filterSliderContainer.setParameters(this.perPagedFilteredLeaderboardKey, leaderboardDTO);
        }
    }
}
