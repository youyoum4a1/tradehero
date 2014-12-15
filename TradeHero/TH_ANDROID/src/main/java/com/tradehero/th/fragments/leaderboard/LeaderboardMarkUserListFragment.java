package com.tradehero.th.fragments.leaderboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.UserOnLeaderboardKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterFragment;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterSliderContainer;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.social.FollowDialogCombo;
import com.tradehero.th.models.user.follow.ChoiceFollowUserAssistantWithDialog;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.persistence.leaderboard.PerPagedFilteredLeaderboardKeyPreference;
import com.tradehero.th.persistence.leaderboard.PerPagedLeaderboardKeyPreference;
import com.tradehero.th.utils.AdapterViewUtils;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.MultiScrollListener;
import com.tradehero.th.widget.list.BaseExpandingItemListener;
import com.tradehero.th.widget.list.SingleExpandingListViewListener;
import dagger.Lazy;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import org.ocpsoft.prettytime.PrettyTime;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

public class LeaderboardMarkUserListFragment extends BaseLeaderboardFragment
{
    public static final String PREFERENCE_KEY_PREFIX = LeaderboardMarkUserListFragment.class.getName();

    @Inject Analytics analytics;
    @Inject Provider<PrettyTime> prettyTime;
    @Inject @ForUser SharedPreferences preferences;
    @Inject Lazy<AdapterViewUtils> adapterViewUtilsLazy;
    @Inject SingleExpandingListViewListener singleExpandingListViewListener;

    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeContainer;
    @InjectView(R.id.leaderboard_mark_user_listview) ListView leaderboardMarkUserListView;
    @InjectView(R.id.leaderboard_mark_user_screen) BetterViewAnimator leaderboardMarkUserScreen;
    protected View headerView;

    private TextView leaderboardMarkUserMarkingTime;
    private View mRankHeaderView;

    @Nullable protected Subscription userOnLeaderboardCacheSubscription;
    protected LeaderboardUserDTO currentLeaderboardUserDTO;

    protected LeaderboardMarkUserLoader leaderboardMarkUserLoader;
    protected LeaderboardMarkUserListAdapter
            leaderboardMarkUserListAdapter;

    protected LeaderboardFilterFragment leaderboardFilterFragment;
    protected PerPagedLeaderboardKeyPreference savedPreference;

    protected PerPagedLeaderboardKey currentLeaderboardKey;
    protected FollowDialogCombo followDialogCombo;
    protected ChoiceFollowUserAssistantWithDialog choiceFollowUserAssistantWithDialog;
    private LeaderboardMarkUserListViewFragmentListLoaderCallback leaderboardMarkUserListViewFragmentListLoaderCallback =
            new LeaderboardMarkUserListViewFragmentListLoaderCallback();

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        currentLeaderboardKey = getInitialLeaderboardKey();
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
        ButterKnife.inject(this, view);
        leaderboardMarkUserListView.setOnItemClickListener(singleExpandingListViewListener);
        inflateHeaderView(inflater);

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

    protected void inflateHeaderView(
            @NonNull LayoutInflater inflater)
    {
        if (leaderboardMarkUserListView != null)
        {
            headerView = inflater.inflate(getHeaderViewResId(), null);
            if (headerView != null)
            {
                leaderboardMarkUserListView.addHeaderView(headerView, null, false);
                initHeaderView();
            }

            View userRankingHeaderView = inflateAndGetUserRankHeaderView();
            setupOwnRankingView(userRankingHeaderView);

            leaderboardMarkUserListView.addHeaderView(userRankingHeaderView);
        }
    }

    @LayoutRes protected int getCurrentRankLayoutResId()
    {
        return R.layout.lbmu_item_own_ranking_roi_mode;
    }

    @LayoutRes protected int getHeaderViewResId()
    {
        return R.layout.leaderboard_listview_header;
    }

    @Override protected void linkWith(LeaderboardDefDTO leaderboardDefDTO, boolean andDisplay)
    {
        super.linkWith(leaderboardDefDTO, andDisplay);
        if (andDisplay)
        {
            initHeaderView();
        }
    }

    protected void initHeaderView()
    {
        if (headerView != null)
        {
            String leaderboardDefDesc = leaderboardDefDTO == null ? null : leaderboardDefDTO.desc;
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
                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_Back));
                break;
            case R.id.button_leaderboard_filter:
                pushFilterFragmentIn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        prepareLeaderboardMarkUserAdapter();
    }

    @NonNull protected LeaderboardMarkUserListAdapter createLeaderboardMarkUserAdapter()
    {
        return new LeaderboardMarkUserListAdapter(getActivity(), leaderboardDefKey.key);
    }

    protected void prepareLeaderboardMarkUserAdapter()
    {
        if (leaderboardMarkUserListAdapter != null)
        {
            leaderboardMarkUserListAdapter.setFollowRequestedListener(null);
        }
        leaderboardMarkUserListAdapter = createLeaderboardMarkUserAdapter();
        leaderboardMarkUserListAdapter.setDTOLoaderCallback(leaderboardMarkUserListViewFragmentListLoaderCallback);
        leaderboardMarkUserListAdapter.setCurrentUserProfileDTO(currentUserProfileDTO);
        leaderboardMarkUserListAdapter.setApplicablePortfolioId(getApplicablePortfolioId());
        leaderboardMarkUserListAdapter.setFollowRequestedListener(new LeaderboardMarkUserListFollowRequestedListener());
        swipeContainer.setOnRefreshListener(leaderboardMarkUserListAdapter);
        leaderboardMarkUserListView.setOnScrollListener(new MultiScrollListener(dashboardBottomTabsListViewScrollListener.get(),
                new AbsListView.OnScrollListener()
                {
                    private boolean scrollStateChanged;

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState)
                    {
                        scrollStateChanged = true;
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                    {
                        if (view instanceof ListView && scrollStateChanged)
                        {
                            ListView listView = (ListView) view;
                            int mTotalHeadersAndFooters = listView.getHeaderViewsCount() + listView.getFooterViewsCount();

                            if (totalItemCount > mTotalHeadersAndFooters && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 1))
                            {
                                scrollStateChanged = false;
                                swipeContainer.setRefreshing(true);
                                leaderboardMarkUserLoader.loadPrevious();
                            }
                        }
                    }
                }));
        leaderboardMarkUserListView.setAdapter(leaderboardMarkUserListAdapter);

        Bundle loaderBundle = new Bundle(getArguments());
        leaderboardMarkUserLoader = (LeaderboardMarkUserLoader) getActivity().getSupportLoaderManager().initLoader(
                leaderboardDefKey.key, loaderBundle, leaderboardMarkUserListAdapter.getLoaderCallback());
        leaderboardMarkUserLoader.setLeaderboardLoaderListener(new LeaderboardLoadedListener());
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchUserOnLeaderboard();
        if (leaderboardFilterFragment != null)
        {
            PerPagedFilteredLeaderboardKey newLeaderboardKey = leaderboardFilterFragment.getPerPagedFilteredLeaderboardKey();
            leaderboardFilterFragment = null;
            Timber.d("%s", newLeaderboardKey.equals(currentLeaderboardKey));

            if (!newLeaderboardKey.equals(currentLeaderboardKey))
            {
                currentLeaderboardKey = newLeaderboardKey;
                swipeContainer.setRefreshing(true);
                initialLoad();
            }
            getActivity().invalidateOptionsMenu();
        }
        else
        {
            Timber.d("onResume filterFragment is null");
        }
    }

    @Override public void onStop()
    {
        unsubscribe(userOnLeaderboardCacheSubscription);
        userOnLeaderboardCacheSubscription = null;
        detachFollowDialogCombo();
        detachChoiceFollowAssistant();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        mRankHeaderView = null;
        headerView = null;
        if (leaderboardMarkUserListAdapter != null)
        {
            leaderboardMarkUserListAdapter.setDTOLoaderCallback(null);
            leaderboardMarkUserListAdapter.setFollowRequestedListener(null);
        }
        leaderboardMarkUserListAdapter = null;

        swipeContainer.setOnRefreshListener(null);
        leaderboardMarkUserListView.setOnScrollListener(null);
        ButterKnife.reset(this);
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

    protected void detachFollowDialogCombo()
    {
        FollowDialogCombo followDialogComboCopy = followDialogCombo;
        if (followDialogComboCopy != null)
        {
            followDialogComboCopy.followDialogView.setFollowRequestedListener(null);
        }
        followDialogCombo = null;
    }

    protected void detachChoiceFollowAssistant()
    {
        ChoiceFollowUserAssistantWithDialog copy = choiceFollowUserAssistantWithDialog;
        if (copy != null)
        {
            copy.onDestroy();
        }
        choiceFollowUserAssistantWithDialog = null;
    }

    @Override protected void linkWithApplicable(OwnedPortfolioId purchaseApplicablePortfolioId, boolean andDisplay)
    {
        super.linkWithApplicable(purchaseApplicablePortfolioId, andDisplay);
        if (leaderboardMarkUserListAdapter != null && purchaseApplicablePortfolioId != null)
        {
            leaderboardMarkUserListAdapter.setApplicablePortfolioId(purchaseApplicablePortfolioId);
        }
    }

    @Nullable protected View getRankHeaderView()
    {
        return mRankHeaderView;
    }

    @Override protected void setCurrentUserProfileDTO(@NonNull UserProfileDTO currentUserProfileDTO)
    {
        super.setCurrentUserProfileDTO(currentUserProfileDTO);
        if (leaderboardMarkUserListAdapter != null)
        {
            leaderboardMarkUserListAdapter.setCurrentUserProfileDTO(currentUserProfileDTO);
        }
        if (mRankHeaderView != null && mRankHeaderView instanceof BaseLeaderboardMarkUserItemView)
        {
            BaseLeaderboardMarkUserItemView ownRankingView = (BaseLeaderboardMarkUserItemView) mRankHeaderView;
            ownRankingView.linkWith(getApplicablePortfolioId());
            ownRankingView.linkWith(currentUserProfileDTO);
        }
    }

    protected void fetchUserOnLeaderboard()
    {
        UserOnLeaderboardKey userOnLeaderboardKey =
                new UserOnLeaderboardKey(new LeaderboardKey(leaderboardDefKey.key), currentUserId.toUserBaseKey());
        unsubscribe(userOnLeaderboardCacheSubscription);
        userOnLeaderboardCacheSubscription = AndroidObservable.bindFragment(
                this,
                leaderboardCache.get(userOnLeaderboardKey))
                .subscribe(createUserOnLeaderboardObserver());
        //Show loading
        updateLoadingCurrentRankHeaderView();
    }

    /**
     * Get the header view which shows the user's current rank
     */
    @Nullable protected final View inflateAndGetUserRankHeaderView()
    {
        if (mRankHeaderView == null)
        {
            mRankHeaderView = LayoutInflater.from(getActivity()).inflate(getCurrentRankLayoutResId(), null, false);
        }
        return mRankHeaderView;
    }

    protected void updateLoadingCurrentRankHeaderView()
    {
        if (mRankHeaderView != null && mRankHeaderView instanceof BaseLeaderboardMarkUserItemView)
        {
            BaseLeaderboardMarkUserItemView leaderboardMarkUserStockItemView = (BaseLeaderboardMarkUserItemView) mRankHeaderView;
            leaderboardMarkUserStockItemView.displayUserIsLoading();
        }
    }

    protected void updateCurrentRankHeaderViewWithLeaderboardUser()
    {
        if (mRankHeaderView != null && mRankHeaderView instanceof BaseLeaderboardMarkUserItemView)
        {
            BaseLeaderboardMarkUserItemView leaderboardMarkUserItemView = (BaseLeaderboardMarkUserItemView) mRankHeaderView;
            if (currentLeaderboardUserDTO != null)
            {
                leaderboardMarkUserItemView.displayUserIsNotRanked();
                // user is not ranked, disable expandable view
                leaderboardMarkUserItemView.setOnClickListener(null);
            }
            if (mRankHeaderView instanceof BaseLeaderboardMarkUserItemView && currentLeaderboardUserDTO instanceof LeaderboardUserDTO)
            {
                BaseLeaderboardMarkUserItemView leaderboardMarkUserStockItemView = (BaseLeaderboardMarkUserItemView) mRankHeaderView;

                leaderboardMarkUserStockItemView.display(currentLeaderboardUserDTO);
                setupOwnRankingView(leaderboardMarkUserStockItemView);
                leaderboardMarkUserStockItemView.setOnClickListener(new BaseExpandingItemListener());
            }
        }
    }

    protected void setupOwnRankingView(View userRankingHeaderView)
    {
        if (userRankingHeaderView instanceof BaseLeaderboardMarkUserItemView)
        {
            BaseLeaderboardMarkUserItemView ownRankingView = (BaseLeaderboardMarkUserItemView) userRankingHeaderView;
            if (ownRankingView.expandingLayout != null)
            {
                ownRankingView.expandingLayout.setVisibility(View.GONE);
                ownRankingView.onExpand(false);
            }
        }
    }

    protected Observer<Pair<LeaderboardKey, LeaderboardDTO>> createUserOnLeaderboardObserver()
    {
        return new BaseLeaderboardFragmentUserOnLeaderboardCacheObserver();
    }

    protected class BaseLeaderboardFragmentUserOnLeaderboardCacheObserver implements Observer<Pair<LeaderboardKey, LeaderboardDTO>>
    {
        @Override public void onNext(Pair<LeaderboardKey, LeaderboardDTO> pair)
        {
            LeaderboardUserDTO received = null;
            if (pair.second.users != null && pair.second.users.size() == 1)
            {
                received = pair.second.users.get(0);
            }
            linkWith(received, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            Timber.e("Failed to download current User position on leaderboard", e);
            THToast.show(R.string.error_fetch_user_on_leaderboard);
            linkWith((LeaderboardUserDTO) null, true);
        }
    }

    protected void linkWith(@Nullable LeaderboardUserDTO leaderboardDTO, boolean andDisplay)
    {
        this.currentLeaderboardUserDTO = leaderboardDTO;
        if (andDisplay)
        {
            updateCurrentRankHeaderViewWithLeaderboardUser();
        }
    }

    public void initialLoad()
    {
        Timber.d("initialLoad %s", currentLeaderboardKey);
        leaderboardMarkUserLoader.setPagedLeaderboardKey(currentLeaderboardKey);
        leaderboardMarkUserLoader.reload();
    }

    private void updateListViewRow(@NonNull final UserBaseKey heroId)
    {
        adapterViewUtilsLazy.get().updateSingleRowWhere(leaderboardMarkUserListView, UserBaseDTO.class,
                userBaseDTO -> userBaseDTO.getBaseKey().equals(heroId));
    }

    protected void pushFilterFragmentIn()
    {
        Bundle args = new Bundle();
        LeaderboardFilterFragment.putPerPagedFilteredLeaderboardKey(args, (PerPagedFilteredLeaderboardKey) currentLeaderboardKey);
        this.leaderboardFilterFragment = navigator.get().pushFragment(LeaderboardFilterFragment.class, args);
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
                                R.drawable.ic_action_icn_actionbar_filteroff :
                                R.drawable.ic_action_icn_actionbar_filteron
                );
            }
            else
            {
                filterIcon.setIcon(R.drawable.ic_action_icn_actionbar_filteroff);
            }
        }
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
            leaderboardMarkUserScreen.setDisplayedChildByLayoutId(R.id.swipe_container);
            swipeContainer.setRefreshing(false);
        }
    }

    protected class LeaderboardLoadedListener implements LeaderboardMarkUserLoader.LeaderboardLoaderListener
    {
        @Override public void onLoadedInBackground(final LeaderboardDTO leaderboardDTO)
        {
            Date markingTime = leaderboardDTO.markUtc;
            if (markingTime != null && leaderboardMarkUserMarkingTime != null)
            {
                leaderboardMarkUserMarkingTime.setText(String.format("(%s)", prettyTime.get().format(markingTime)));
            }
            leaderboardMarkUserListAdapter.setIsForex(leaderboardDTO.isForex);
            if (mRankHeaderView != null && mRankHeaderView instanceof BaseLeaderboardMarkUserItemView)
            {
                ((BaseLeaderboardMarkUserItemView) mRankHeaderView).shouldHideStatistics(leaderboardDTO.isForex);
            }
        }
    }

    protected class LeaderboardMarkUserListFollowRequestedListener implements BaseLeaderboardMarkUserItemView.OnFollowRequestedListener
    {
        @Override public void onFollowRequested(@NonNull UserBaseDTO userBaseDTO)
        {
            handleFollowRequested(userBaseDTO);
        }
    }

    protected void handleFollowRequested(@NonNull final UserBaseDTO userBaseDTO)
    {
        detachChoiceFollowAssistant();
        choiceFollowUserAssistantWithDialog = new ChoiceFollowUserAssistantWithDialog(
                getActivity(),
                userBaseDTO.getBaseKey(),
                createUserFollowedListener(),
                getApplicablePortfolioId());
        choiceFollowUserAssistantWithDialog.setHeroBaseInfo(userBaseDTO);
        choiceFollowUserAssistantWithDialog.launchChoice();
    }

    @NonNull protected SimpleFollowUserAssistant.OnUserFollowedListener createUserFollowedListener()
    {
        return new LeaderboardMarkUserListOnUserFollowedListener();
    }

    protected class LeaderboardMarkUserListOnUserFollowedListener implements SimpleFollowUserAssistant.OnUserFollowedListener
    {
        @Override public void onUserFollowSuccess(@NonNull UserBaseKey userFollowed, @NonNull UserProfileDTO currentUserProfileDTO)
        {
            setCurrentUserProfileDTO(currentUserProfileDTO);
            int followType = currentUserProfileDTO.getFollowType(userFollowed);
            if (followType == UserProfileDTOUtil.IS_FREE_FOLLOWER)
            {
                analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.FreeFollow_Success, AnalyticsConstants.Leaderboard));
            }
            else if (followType == UserProfileDTOUtil.IS_PREMIUM_FOLLOWER)
            {
                analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.PremiumFollow_Success, AnalyticsConstants.Leaderboard));
            }
            updateListViewRow(userFollowed);
        }

        @Override public void onUserFollowFailed(@NonNull UserBaseKey userFollowed, @NonNull Throwable error)
        {
            THToast.show(new THException(error));
        }
    }
}
