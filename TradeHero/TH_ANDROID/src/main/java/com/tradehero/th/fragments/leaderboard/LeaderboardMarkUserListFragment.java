package com.tradehero.th.fragments.leaderboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
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
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.social.FollowDialogCombo;
import com.tradehero.th.models.user.follow.ChoiceFollowUserAssistantWithDialog;
import com.tradehero.th.persistence.leaderboard.LeaderboardCacheRx;
import com.tradehero.th.persistence.leaderboard.PagedLeaderboardWrapperCacheRx;
import com.tradehero.th.persistence.leaderboard.PerPagedFilteredLeaderboardKeyPreference;
import com.tradehero.th.persistence.leaderboard.PerPagedLeaderboardKeyPreference;
import com.tradehero.th.utils.AdapterViewUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.MultiScrollListener;
import com.tradehero.th.widget.list.BaseExpandingItemListener;
import com.tradehero.th.widget.list.SingleExpandingListViewListener;
import dagger.Lazy;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Provider;
import org.ocpsoft.prettytime.PrettyTime;
import rx.android.app.AppObservable;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

public class LeaderboardMarkUserListFragment extends BaseLeaderboardPagedListRxFragment<
        PagedLeaderboardKey,
        LeaderboardUserDTO,
        LeaderboardUserDTOList,
        LeaderboardDTO>
{
    public static final String PREFERENCE_KEY_PREFIX = LeaderboardMarkUserListFragment.class.getName();
    private static final String BUNDLE_KEY_LEADERBOARD_TYPE_ID = LeaderboardMarkUserListFragment.class.getName() + ".leaderboardTypeId";

    @Inject Analytics analytics;
    @Inject Provider<PrettyTime> prettyTime;
    @Inject @ForUser SharedPreferences preferences;
    @Inject Lazy<AdapterViewUtils> adapterViewUtilsLazy;
    @Inject SingleExpandingListViewListener singleExpandingListViewListener;
    @Inject PagedLeaderboardWrapperCacheRx pagedLeaderboardWrapperCache;
    @Inject LeaderboardCacheRx leaderboardCache;

    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeContainer;
    protected View headerView;

    private TextView leaderboardMarkUserMarkingTime;
    private View mRankHeaderView;

    protected LeaderboardUserDTO currentLeaderboardUserDTO;

    protected LeaderboardFilterFragment leaderboardFilterFragment;
    protected PerPagedLeaderboardKeyPreference savedPreference;

    protected PerPagedLeaderboardKey currentLeaderboardKey;
    protected LeaderboardType currentLeaderboardType;
    protected FollowDialogCombo followDialogCombo;

    public static void putLeaderboardType(@NonNull Bundle args, @NonNull LeaderboardType leaderboardType)
    {
        args.putInt(BUNDLE_KEY_LEADERBOARD_TYPE_ID, leaderboardType.getLeaderboardTypeId());
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        subscriptions = new SubscriptionList();
        currentLeaderboardKey = getInitialLeaderboardKey();
        currentLeaderboardType = getInitialLeaderboardType();
        if (currentLeaderboardType != null && currentLeaderboardType.getAssetClass() != null)
        {
            currentLeaderboardKey.setAssetClass(currentLeaderboardType.getAssetClass());
        }
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

    protected LeaderboardType getInitialLeaderboardType()
    {
        if (getArguments() != null && getArguments().getInt(BUNDLE_KEY_LEADERBOARD_TYPE_ID) != 0)
        {
            int val = getArguments().getInt(BUNDLE_KEY_LEADERBOARD_TYPE_ID);
            return LeaderboardType.from(val);
        }
        return null;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_mark_user_listview, container, false);
        ButterKnife.inject(this, view);
        inflateHeaderView(inflater);
        listView.setEmptyView(inflateEmptyView(inflater, container));
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
        headerView = inflater.inflate(getHeaderViewResId(), null);
        if (headerView != null)
        {
            ((ListView) listView).addHeaderView(headerView, null, false);
            initHeaderView();
        }

        View userRankingHeaderView = inflateAndGetUserRankHeaderView();
        setupOwnRankingView(userRankingHeaderView);
        ((ListView) listView).addHeaderView(userRankingHeaderView);
    }

    @LayoutRes protected int getHeaderViewResId()
    {
        return R.layout.leaderboard_listview_header;
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

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        swipeContainer.setOnRefreshListener(() -> leaderboardCache.get(new PagedLeaderboardKey(leaderboardDefKey.key, 1)));
    }

    @Override public void onStart()
    {
        super.onStart();
        subscriptions.add(((LeaderboardMarkUserListAdapter) itemViewAdapter).getFollowRequestedObservable()
                .subscribe(
                        this::handleFollowRequested,
                        e -> Timber.e(e, "Error when receiving user follow requested")));
        requestDtos();
        fetchUserOnLeaderboard();
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
                swipeContainer.setRefreshing(true);
                requestDtos();
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
        detachFollowDialogCombo();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        mRankHeaderView = null;
        headerView = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.leaderboardFilterFragment = null;
        saveCurrentFilterKey();
        super.onDestroy();
    }

    @NonNull protected LeaderboardMarkUserListAdapter createItemViewAdapter()
    {
        LeaderboardMarkUserListAdapter adapter = new LeaderboardMarkUserListAdapter(
                getActivity(),
                R.layout.lbmu_item_roi_mode,
                new LeaderboardKey(leaderboardDefKey.key));
        adapter.setCurrentUserProfileDTO(currentUserProfileDTO);
        adapter.setApplicablePortfolioId(getApplicablePortfolioId());
        return adapter;
    }

    @NonNull @Override protected AbsListView.OnScrollListener createListViewScrollListener()
    {
        return new MultiScrollListener(
                super.createListViewScrollListener(),
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
                                // TODO load previous page
                            }
                        }
                    }
                });
    }

    @Override public boolean canMakePagedDtoKey()
    {
        return true;
    }

    @NonNull @Override public PagedLeaderboardKey makePagedDtoKey(int page)
    {
        return currentLeaderboardKey.cloneAtPage(page);
    }

    @NonNull @Override public DTOCacheRx<PagedLeaderboardKey, LeaderboardDTO> getCache()
    {
        return pagedLeaderboardWrapperCache;
    }

    @Override protected void linkWith(LeaderboardDefDTO leaderboardDefDTO)
    {
        super.linkWith(leaderboardDefDTO);
        initHeaderView();
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

    @Override protected void linkWithApplicable(OwnedPortfolioId purchaseApplicablePortfolioId, boolean andDisplay)
    {
        super.linkWithApplicable(purchaseApplicablePortfolioId, andDisplay);
        if (purchaseApplicablePortfolioId != null)
        {
            ((LeaderboardMarkUserListAdapter) itemViewAdapter).setApplicablePortfolioId(purchaseApplicablePortfolioId);
        }
    }

    @Nullable protected View getRankHeaderView()
    {
        return mRankHeaderView;
    }

    @Override protected void setCurrentUserProfileDTO(@NonNull UserProfileDTO currentUserProfileDTO)
    {
        super.setCurrentUserProfileDTO(currentUserProfileDTO);
        ((LeaderboardMarkUserListAdapter) itemViewAdapter).setCurrentUserProfileDTO(currentUserProfileDTO);
        if (mRankHeaderView instanceof LeaderboardMarkUserItemView)
        {
            LeaderboardMarkUserItemView ownRankingView = (LeaderboardMarkUserItemView) mRankHeaderView;
            ownRankingView.linkWith(getApplicablePortfolioId());
            ownRankingView.linkWith(currentUserProfileDTO);
        }
    }

    protected void fetchUserOnLeaderboard()
    {
        UserOnLeaderboardKey userOnLeaderboardKey =
                new UserOnLeaderboardKey(
                        new LeaderboardKey(leaderboardDefKey.key, currentLeaderboardType != null ? currentLeaderboardType.getAssetClass() : null),
                        currentUserId.toUserBaseKey());
        subscriptions.add(AppObservable.bindFragment(
                this,
                leaderboardCache.get(userOnLeaderboardKey)
                        .map(new PairGetSecond<>())
                        .map(leaderboard -> {
                            LeaderboardUserDTO received = null;
                            if (leaderboard.users != null && leaderboard.users.size() == 1)
                            {
                                received = leaderboard.users.get(0);
                            }
                            return received;
                        }))
                .subscribe(
                        this::linkWith,
                        e -> {
                            Timber.e("Failed to download current User position on leaderboard", e);
                            THToast.show(R.string.error_fetch_user_on_leaderboard);
                            linkWith((LeaderboardUserDTO) null);
                        }));
        //Show loading
        updateLoadingCurrentRankHeaderView();
    }

    protected void linkWith(@Nullable LeaderboardUserDTO leaderboardUserDTO)
    {
        this.currentLeaderboardUserDTO = leaderboardUserDTO;
        updateCurrentRankHeaderViewWithLeaderboardUser();
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

    @LayoutRes protected int getCurrentRankLayoutResId()
    {
        return R.layout.lbmu_item_own_ranking_roi_mode;
    }

    protected void updateLoadingCurrentRankHeaderView()
    {
        if (mRankHeaderView != null && mRankHeaderView instanceof LeaderboardMarkUserItemView)
        {
            LeaderboardMarkUserItemView leaderboardMarkUserItemView = (LeaderboardMarkUserItemView) mRankHeaderView;
            leaderboardMarkUserItemView.displayUserIsLoading();
        }
    }

    protected void updateCurrentRankHeaderViewWithLeaderboardUser()
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

    private void updateListViewRow(@NonNull final UserBaseKey heroId)
    {
        adapterViewUtilsLazy.get().updateSingleRowWhere(
                listView,
                UserBaseDTO.class,
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

    @Override protected void onNext(@NonNull PagedLeaderboardKey key, @NonNull LeaderboardDTO value)
    {
        super.onNext(key, value);
        Date markingTime = value.markUtc;
        if (markingTime != null && leaderboardMarkUserMarkingTime != null)
        {
            leaderboardMarkUserMarkingTime.setText(String.format("(%s)", prettyTime.get().format(markingTime)));
        }
    }

    protected void handleFollowRequested(@NonNull final UserBaseDTO userBaseDTO)
    {
        subscriptions.add(AppObservable.bindFragment(
                this,
                new ChoiceFollowUserAssistantWithDialog(
                        getActivity(),
                        userBaseDTO,
                        getApplicablePortfolioId()).launchChoiceRx())
                .subscribe(
                        pair -> {
                            setCurrentUserProfileDTO(pair.second);
                            int followType = pair.second.getFollowType(userBaseDTO);
                            if (followType == UserProfileDTOUtil.IS_FREE_FOLLOWER)
                            {
                                analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.FreeFollow_Success, AnalyticsConstants.Leaderboard));
                            }
                            else if (followType == UserProfileDTOUtil.IS_PREMIUM_FOLLOWER)
                            {
                                analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.PremiumFollow_Success, AnalyticsConstants.Leaderboard));
                            }
                            updateListViewRow(userBaseDTO.getBaseKey());
                        },
                        error -> THToast.show(new THException(error))
                ));
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        singleExpandingListViewListener.onItemClick(parent, view, position, id);
        super.onItemClick(parent, view, position, id);
    }
}
