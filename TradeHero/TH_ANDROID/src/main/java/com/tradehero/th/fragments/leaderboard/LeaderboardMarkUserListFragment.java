package com.tradehero.th.fragments.leaderboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterFragment;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterSliderContainer;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.persistence.leaderboard.PerPagedFilteredLeaderboardKeyPreference;
import com.tradehero.th.persistence.leaderboard.PerPagedLeaderboardKeyPreference;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import org.ocpsoft.prettytime.PrettyTime;
import timber.log.Timber;

public class LeaderboardMarkUserListFragment extends BaseLeaderboardFragment
{
    public static final String PREFERENCE_KEY_PREFIX = LeaderboardMarkUserListFragment.class.getName();

    @Inject protected LocalyticsSession localyticsSession;

    @Inject Provider<PrettyTime> prettyTime;
    @Inject SharedPreferences preferences;

    @InjectView(R.id.leaderboard_mark_user_listview) LeaderboardMarkUserListView leaderboardMarkUserListView;
    @InjectView(R.id.leaderboard_mark_user_screen) BetterViewAnimator leaderboardMarkUserScreen;

    private TextView leaderboardMarkUserMarkingTime;

    protected LeaderboardMarkUserLoader leaderboardMarkUserLoader;
    protected LeaderboardMarkUserListAdapter leaderboardMarkUserListAdapter;

    protected LeaderboardFilterFragment leaderboardFilterFragment;

    protected PerPagedLeaderboardKeyPreference savedPreference;
    protected PerPagedLeaderboardKey currentLeaderboardKey;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        currentLeaderboardKey = getInitialLeaderboardKey();
    }

    @Override protected PremiumFollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        return new LeaderboardMarkUserListPremiumUserFollowedListener();
    }

    protected PerPagedLeaderboardKey getInitialLeaderboardKey()
    {
        savedPreference = new PerPagedFilteredLeaderboardKeyPreference(
                getActivity(),
                preferences,
                PREFERENCE_KEY_PREFIX + leaderboardDefKey,
                LeaderboardFilterSliderContainer.getStartingFilter(getResources(), leaderboardDefKey.key).getFilterStringSet());
        PerPagedFilteredLeaderboardKey initialKey = ((PerPagedFilteredLeaderboardKeyPreference) savedPreference)
                .getPerPagedFilteredLeaderboardKey();
        // We override here to make sure we do not pick up key, page or perPage from the preference
        return new PerPagedFilteredLeaderboardKey(initialKey, leaderboardDefKey.key, null, null);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_mark_user_listview, container, false);
        initViews(view);
        inflateHeaderView(inflater, container);

        if (leaderboardMarkUserListView != null)
        {
            leaderboardMarkUserListView.setEmptyView(inflateEmptyView(inflater, container));
        }
        return view;
    }

    protected View inflateEmptyView(LayoutInflater inflater, ViewGroup container)
    {
        /*
         TODO I haven't seen a basic leaderboard empty view except the one from Friend leaderboard
         on iOS app, therefore, I will use a dummy empty view here
         */
        return inflater.inflate(R.layout.leaderboard_empty_view, container, false);
    }

    protected void inflateHeaderView(LayoutInflater inflater, ViewGroup container)
    {
        if (leaderboardMarkUserListView != null)
        {
            View headerView = inflater.inflate(getHeaderViewResId(), null);
            if (headerView != null)
            {
                leaderboardMarkUserListView.getRefreshableView().addHeaderView(headerView, null, false);
                initHeaderView(headerView);
            }
        }
    }

    protected int getHeaderViewResId()
    {
        return R.layout.leaderboard_listview_header;
    }

    protected void initHeaderView(View headerView)
    {
        String leaderboardDefDesc = getArguments().getString(BUNDLE_KEY_LEADERBOARD_DEF_DESC);

        TextView leaderboardMarkUserTimePeriod = (TextView) headerView.findViewById(R.id.leaderboard_time_period);
        if (leaderboardMarkUserTimePeriod != null)
        {
            if (leaderboardDefDesc != null)
            {
                leaderboardMarkUserTimePeriod.setText(leaderboardDefDesc);
                leaderboardMarkUserTimePeriod.setVisibility(View.VISIBLE);
            }
            else
            {
                leaderboardMarkUserTimePeriod.setVisibility(View.GONE);
            }
        }
        leaderboardMarkUserMarkingTime = (TextView) headerView.findViewById(R.id.leaderboard_marking_time);
    }

    //<editor-fold desc="ActionBar">
    @Override protected int getMenuResource()
    {
        return R.menu.leaderboard_listview_menu;
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        displayFilterIcon(menu.findItem(R.id.leaderboard_listview_menu_help));
        super.onPrepareOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                localyticsSession.tagEvent(LocalyticsConstants.Leaderboard_Back);
                break;
            case R.id.button_leaderboard_filter:
                pushFilterFragmentIn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Override protected void initViews(View view)
    {
        ButterKnife.inject(this, view);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (leaderboardMarkUserListAdapter == null)
        {
            leaderboardMarkUserListAdapter = createLeaderboardMarkUserAdapter();
            leaderboardMarkUserListAdapter.setDTOLoaderCallback(new LeaderboardMarkUserListViewFragmentListLoaderCallback());
            leaderboardMarkUserListAdapter.setCurrentUserProfileDTO(currentUserProfileDTO);
            leaderboardMarkUserListAdapter.setFollowRequestedListener(new LeaderboardMarkUserListFollowRequestedListener());
            leaderboardMarkUserListView.setOnRefreshListener(leaderboardMarkUserListAdapter);
            leaderboardMarkUserListView.setAdapter(leaderboardMarkUserListAdapter);
        }

        Bundle loaderBundle = new Bundle(getArguments());
        leaderboardMarkUserLoader = (LeaderboardMarkUserLoader) getActivity().getSupportLoaderManager().initLoader(
                leaderboardDefKey.key, loaderBundle, leaderboardMarkUserListAdapter.getLoaderCallback());
    }

    protected LeaderboardMarkUserListAdapter createLeaderboardMarkUserAdapter()
    {
        return new LeaderboardMarkUserListAdapter(
                getActivity(), getActivity().getLayoutInflater(), leaderboardDefKey.key, R.layout.lbmu_item_roi_mode);
    }

    @Override public void onResume()
    {
        super.onResume();
        if (leaderboardFilterFragment != null)
        {
            PerPagedFilteredLeaderboardKey newLeaderboardKey = leaderboardFilterFragment.getPerPagedFilteredLeaderboardKey();
            leaderboardFilterFragment = null;
            Timber.d("%s", newLeaderboardKey.equals(currentLeaderboardKey));

            if (!newLeaderboardKey.equals(currentLeaderboardKey))
            {
                currentLeaderboardKey = newLeaderboardKey;
                leaderboardMarkUserListView.setRefreshing();
                initialLoad();
            }
            getActivity().invalidateOptionsMenu();
        }
        else
        {
            Timber.d("onResume filterFragment is null");
        }
    }

    @Override public void onDestroyView()
    {
        if (leaderboardMarkUserListAdapter != null)
        {
            leaderboardMarkUserListAdapter.setDTOLoaderCallback(null);
            leaderboardMarkUserListAdapter.setFollowRequestedListener(null);
        }
        leaderboardMarkUserListAdapter = null;

        if (leaderboardMarkUserListView != null)
        {
            leaderboardMarkUserListView.setOnRefreshListener((LeaderboardMarkUserListAdapter) null);
        }
        leaderboardMarkUserListView = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.leaderboardFilterFragment = null;
        saveCurrentFilterKey();
        getActivity().getSupportLoaderManager().destroyLoader(leaderboardDefKey.key);
        super.onDestroy();
    }

    protected void saveCurrentFilterKey()
    {
        savedPreference.set(currentLeaderboardKey);
    }

    @Override protected void setCurrentUserProfileDTO(UserProfileDTO currentUserProfileDTO)
    {
        super.setCurrentUserProfileDTO(currentUserProfileDTO);
        if (leaderboardMarkUserListAdapter != null)
        {
            leaderboardMarkUserListAdapter.setCurrentUserProfileDTO(currentUserProfileDTO);
            leaderboardMarkUserListAdapter.notifyDataSetChanged();
        }
    }

    public void initialLoad()
    {
        Timber.d("initialLoad %s", currentLeaderboardKey);
        leaderboardMarkUserLoader.setPagedLeaderboardKey(currentLeaderboardKey);
        leaderboardMarkUserLoader.reload();
        //invalidateCachedItemView();
    }

    /**
     * http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.0.3_r1/android/widget/ListView.java#443
     * this crazy way works and is the only way I found to clear ListView's recycle (reusing item view)
     */

    /**
     * Update 22 Feb 2014: We are not using different mode for leaderboard item type anymore,
     * Instead, filter mode feature is implemented, therefore, no need to clear listview's recycle!!!
     */
    @Deprecated
    protected void invalidateCachedItemView()
    {
        leaderboardMarkUserListView.setAdapter(leaderboardMarkUserListAdapter);
    }

    protected void pushFilterFragmentIn()
    {
        Bundle args = new Bundle();
        LeaderboardFilterFragment.putPerPagedFilteredLeaderboardKey(args, (PerPagedFilteredLeaderboardKey) currentLeaderboardKey);
        this.leaderboardFilterFragment = getDashboardNavigator().pushFragment(LeaderboardFilterFragment.class, args);
    }

    protected void displayFilterIcon(MenuItem filterIcon)
    {
        if (filterIcon != null)
        {
            if (currentLeaderboardKey instanceof PerPagedFilteredLeaderboardKey)
            {
                boolean areEqual = LeaderboardFilterSliderContainer.areInnerValuesEqualToStarting(
                        getResources(),
                        (PerPagedFilteredLeaderboardKey) currentLeaderboardKey);
                filterIcon.setIcon(
                         areEqual ?
                            R.drawable.filter :
                            R.drawable.filter_active
                );
            }
            else
            {
                filterIcon.setIcon(R.drawable.filter);
            }
        }
    }

    protected void handleFollowRequested(final UserBaseKey userBaseKey)
    {
        //TODO hacked by alipay alex
        //heroAlertDialogUtil.popAlertFollowHero(getActivity(), new DialogInterface.OnClickListener()
        //{
        //    @Override public void onClick(DialogInterface dialog, int which)
        //    {
                premiumFollowUser(userBaseKey);
        //    }
        //});
    }

    protected void handleFollowSuccess(UserProfileDTO userProfileDTO)
    {
        setCurrentUserProfileDTO(userProfileDTO);
    }

    protected class LeaderboardMarkUserListViewFragmentListLoaderCallback extends LoaderDTOAdapter.ListLoaderCallback<LeaderboardUserDTO>
    {
        @Override public ListLoader<LeaderboardUserDTO> onCreateLoader(Bundle args)
        {
            LeaderboardMarkUserLoader leaderboardMarkUserLoader = new LeaderboardMarkUserLoader(getActivity(), currentLeaderboardKey);
            leaderboardMarkUserLoader.setPerPage(Constants.LEADERBOARD_MARK_USER_ITEM_PER_PAGE);
            return leaderboardMarkUserLoader;
        }

        @Override public void onLoadFinished(ListLoader<LeaderboardUserDTO> loader, List<LeaderboardUserDTO> data)
        {
            // display marking time
            LeaderboardMarkUserLoader leaderboardMarkUserLoader = (LeaderboardMarkUserLoader) loader;
            Date markingTime = leaderboardMarkUserLoader.getMarkUtc();
            if (markingTime != null && leaderboardMarkUserMarkingTime != null)
            {
                leaderboardMarkUserMarkingTime.setText(String.format("(%s)", prettyTime.get().format(markingTime)));
            }
            leaderboardMarkUserScreen.setDisplayedChildByLayoutId(R.id.leaderboard_mark_user_listview);
            leaderboardMarkUserListView.onRefreshComplete();
        }
    }

    protected class LeaderboardMarkUserListFollowRequestedListener implements LeaderboardMarkUserItemView.OnFollowRequestedListener
    {
        @Override public void onFollowRequested(UserBaseKey userBaseKey)
        {
            handleFollowRequested(userBaseKey);
        }
    }

    protected class LeaderboardMarkUserListPremiumUserFollowedListener extends BasePurchaseManagerPremiumUserFollowedListener
    {
        @Override public void onUserFollowSuccess(UserBaseKey userFollowed, UserProfileDTO currentUserProfileDTO)
        {
            super.onUserFollowSuccess(userFollowed, currentUserProfileDTO);
            handleFollowSuccess(currentUserProfileDTO);
        }
    }
}
