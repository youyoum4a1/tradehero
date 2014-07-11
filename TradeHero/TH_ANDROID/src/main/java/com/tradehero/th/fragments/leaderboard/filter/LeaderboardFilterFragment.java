package com.tradehero.th.fragments.leaderboard.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.localytics.android.LocalyticsSession;
import com.tradehero.thm.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LeaderboardFilterFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_PER_PAGED_FILTERED_LEADERBOARD_KEY_BUNDLE = LeaderboardFilterFragment.class.getName() + ".perPagedFilteredLeaderboardKey";

    @Inject LocalyticsSession localyticsSession;
    @Inject LeaderboardCache leaderboardCache;
    @InjectView(R.id.leaderboard_filter_slider_container) LeaderboardFilterSliderContainer filterSliderContainer;

    @NotNull protected PerPagedFilteredLeaderboardKey perPagedFilteredLeaderboardKey;
    @Nullable protected LeaderboardDTO leaderboardDTO;

    public static void putPerPagedFilteredLeaderboardKey(@NotNull Bundle args, @NotNull PerPagedFilteredLeaderboardKey key)
    {
        args.putBundle(BUNDLE_KEY_PER_PAGED_FILTERED_LEADERBOARD_KEY_BUNDLE, key.getArgs());
    }

    @NotNull
    private static PerPagedFilteredLeaderboardKey getPerPagedFilteredLeaderboardKey(@NotNull Bundle args)
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
        leaderboardDTO = leaderboardCache.get(perPagedFilteredLeaderboardKey);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_leaderboard_filter, container, false);
        ButterKnife.inject(this, view);
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
                localyticsSession.tagEvent(LocalyticsConstants.Leaderboard_FilterReset);
                break;

            case R.id.btn_leaderboard_filter_confirm:
                localyticsSession.tagEvent(LocalyticsConstants.Leaderboard_FilterDone);
                returnToLeaderboard();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.Leaderboard_FilterShow);
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

    protected void returnToLeaderboard()
    {
        collectPagedFilteredLeaderboardKey();
        getDashboardNavigator().popFragment();
    }
}
