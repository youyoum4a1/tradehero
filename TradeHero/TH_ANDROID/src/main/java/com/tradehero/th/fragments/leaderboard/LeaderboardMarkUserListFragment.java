package com.tradehero.th.fragments.leaderboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.internal.util.Predicate;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
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
import com.tradehero.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.list.BaseExpandingItemListener;
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

    @InjectView(R.id.leaderboard_mark_user_listview) LeaderboardMarkUserListView leaderboardMarkUserListView;
    @InjectView(R.id.leaderboard_mark_user_screen) BetterViewAnimator leaderboardMarkUserScreen;
    protected View headerView;

    private TextView leaderboardMarkUserMarkingTime;
    private View mRankHeaderView;

    @Nullable protected Subscription userOnLeaderboardCacheSubscription;
    protected LeaderboardUserDTO currentLeaderboardUserDTO;

    protected LeaderboardMarkUserLoader leaderboardMarkUserLoader;
    protected LeaderboardMarkUserListAdapter leaderboardMarkUserListAdapter;

    protected LeaderboardFilterFragment leaderboardFilterFragment;

    protected PerPagedLeaderboardKeyPreference savedPreference;
    protected PerPagedLeaderboardKey currentLeaderboardKey;

    protected FollowDialogCombo followDialogCombo;
    protected ChoiceFollowUserAssistantWithDialog choiceFollowUserAssistantWithDialog;

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

    protected void inflateHeaderView(
            @NonNull LayoutInflater inflater,
            @SuppressWarnings("UnusedParameters") ViewGroup container)
    {
        if (leaderboardMarkUserListView != null)
        {
            headerView = inflater.inflate(getHeaderViewResId(), null);
            if (headerView != null)
            {
                leaderboardMarkUserListView.getRefreshableView().addHeaderView(headerView, null, false);
                initHeaderView();
            }

            View userRankingHeaderView = inflateAndGetUserRankHeaderView();
            setupOwnRankingView(userRankingHeaderView);

            leaderboardMarkUserListView.getRefreshableView().addHeaderView(userRankingHeaderView);
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

    @Override protected void initViews(View view)
    {
        ButterKnife.inject(this, view);
        leaderboardMarkUserListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        prepareLeaderboardMarkUserAdapter();
    }

    @NonNull protected LeaderboardMarkUserListAdapter createLeaderboardMarkUserAdapter()
    {
        return new LeaderboardMarkUserListAdapter(
                getActivity(), leaderboardDefKey.key, R.layout.lbmu_item_roi_mode);
    }

    protected void prepareLeaderboardMarkUserAdapter()
    {
        if (leaderboardMarkUserListAdapter != null)
        {
            leaderboardMarkUserListAdapter.setFollowRequestedListener(null);
        }
        leaderboardMarkUserListAdapter = createLeaderboardMarkUserAdapter();
        leaderboardMarkUserListAdapter.setDTOLoaderCallback(new LeaderboardMarkUserListViewFragmentListLoaderCallback());
        leaderboardMarkUserListAdapter.setCurrentUserProfileDTO(currentUserProfileDTO);
        leaderboardMarkUserListAdapter.setApplicablePortfolioId(getApplicablePortfolioId());
        leaderboardMarkUserListAdapter.setFollowRequestedListener(new LeaderboardMarkUserListFollowRequestedListener());
        leaderboardMarkUserListView.setOnRefreshListener(leaderboardMarkUserListAdapter);
        leaderboardMarkUserListView.setAdapter(leaderboardMarkUserListAdapter);

        Bundle loaderBundle = new Bundle(getArguments());
        leaderboardMarkUserLoader = (LeaderboardMarkUserLoader) getActivity().getSupportLoaderManager().initLoader(
                leaderboardDefKey.key, loaderBundle, leaderboardMarkUserListAdapter.getLoaderCallback());
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

        leaderboardMarkUserListView.setOnRefreshListener((LeaderboardMarkUserListAdapter) null);
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
        if (mRankHeaderView != null && mRankHeaderView instanceof LeaderboardMarkUserItemView)
        {
            LeaderboardMarkUserItemView ownRankingView = (LeaderboardMarkUserItemView) mRankHeaderView;
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
        if (mRankHeaderView != null && mRankHeaderView instanceof LeaderboardMarkUserItemView)
        {
            LeaderboardMarkUserItemView leaderboardMarkUserItemView = (LeaderboardMarkUserItemView) mRankHeaderView;
            leaderboardMarkUserItemView.displayUserIsLoading();
        }
    }

    protected void updateCurrentRankHeaderView()
    {
        if (mRankHeaderView != null && mRankHeaderView instanceof LeaderboardMarkUserItemView)
        {
            LeaderboardMarkUserItemView leaderboardMarkUserItemView = (LeaderboardMarkUserItemView) mRankHeaderView;
            if (currentLeaderboardUserDTO != null)
            {
                leaderboardMarkUserItemView.display(currentLeaderboardUserDTO);
                setupOwnRankingView(leaderboardMarkUserItemView);
                leaderboardMarkUserItemView.setOnClickListener(new BaseExpandingItemListener());
            }
            else
            {
                leaderboardMarkUserItemView.displayUserIsNotRanked();
                // user is not ranked, disable expandable view
                leaderboardMarkUserItemView.setOnClickListener(null);
            }
        }
    }

    protected void setupOwnRankingView(View userRankingHeaderView)
    {
        if (userRankingHeaderView instanceof LeaderboardMarkUserItemView)
        {
            LeaderboardMarkUserItemView ownRankingView = (LeaderboardMarkUserItemView) userRankingHeaderView;
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
            updateCurrentRankHeaderView();
        }
    }

    public void initialLoad()
    {
        Timber.d("initialLoad %s", currentLeaderboardKey);
        leaderboardMarkUserLoader.setPagedLeaderboardKey(currentLeaderboardKey);
        leaderboardMarkUserLoader.reload();
        //invalidateCachedItemView();
    }

    private void updateListViewRow(@NonNull final UserBaseKey heroId)
    {
        AdapterView list = leaderboardMarkUserListView.getRefreshableView();
        adapterViewUtilsLazy.get().updateSingleRowWhere(list, UserBaseDTO.class, new Predicate<UserBaseDTO>()
        {
            @Override public boolean apply(@NonNull UserBaseDTO userBaseDTO)
            {
                return userBaseDTO.getBaseKey().equals(heroId);
            }
        });
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
        @Override public void onFollowRequested(UserBaseDTO userBaseDTO)
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
