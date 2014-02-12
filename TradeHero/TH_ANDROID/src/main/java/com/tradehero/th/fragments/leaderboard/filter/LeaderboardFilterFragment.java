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
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by xavier on 2/12/14.
 */
public class LeaderboardFilterFragment extends DashboardFragment
{
    public static final String TAG = LeaderboardFilterFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_PER_PAGED_FILTERED_LEADERBOARD_KEY_BUNDLE = LeaderboardFilterFragment.class.getName() + ".perPagedFilteredLeaderboardKey";

    @InjectView(R.id.leaderboard_filter_slider_container) protected LeaderboardFilterSliderContainer filterSliderContainer;

    protected PerPagedFilteredLeaderboardKey perPagedFilteredLeaderboardKey;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            this.perPagedFilteredLeaderboardKey = new PerPagedFilteredLeaderboardKey(savedInstanceState.getBundle(BUNDLE_KEY_PER_PAGED_FILTERED_LEADERBOARD_KEY_BUNDLE));
        }
        else
        {
            this.perPagedFilteredLeaderboardKey = new PerPagedFilteredLeaderboardKey(getArguments().getBundle(BUNDLE_KEY_PER_PAGED_FILTERED_LEADERBOARD_KEY_BUNDLE));
        }
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

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setTitle(getString(R.string.leaderboard_filter_menu_title));

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_leaderboard_filter_confirm:
                returnToLeaderboard();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onPause()
    {
        collectPagedFilteredLeaderboardKey();
        super.onPause();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBundle(BUNDLE_KEY_PER_PAGED_FILTERED_LEADERBOARD_KEY_BUNDLE, this.perPagedFilteredLeaderboardKey.getArgs());
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
            filterSliderContainer.setFilteredLeaderboardKey(this.perPagedFilteredLeaderboardKey);
        }
    }

    protected void returnToLeaderboard()
    {
        collectPagedFilteredLeaderboardKey();
        getNavigator().popFragment();
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
