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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.internal.util.Predicate;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.UserOnLeaderboardKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterFragment;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterSliderContainer;
import com.tradehero.th.persistence.leaderboard.LeaderboardCacheRx;
import com.tradehero.th.persistence.leaderboard.PerPagedFilteredLeaderboardKeyPreference;
import com.tradehero.th.persistence.leaderboard.PerPagedLeaderboardKeyPreference;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.utils.AdapterViewUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.MultiScrollListener;
import com.tradehero.th.widget.list.SingleExpandingListViewListener;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class LeaderboardMarkUserListFragment extends BaseLeaderboardPagedListRxFragment<
        PagedLeaderboardKey,
        LeaderboardMarkUserItemView.DTO,
        LeaderboardMarkUserItemView.DTOList,
        LeaderboardMarkUserItemView.DTOList>
{
    public static final String PREFERENCE_KEY_PREFIX = LeaderboardMarkUserListFragment.class.getName();
    private static final String BUNDLE_KEY_LEADERBOARD_TYPE_ID = LeaderboardMarkUserListFragment.class.getName() + ".leaderboardTypeId";

    @Inject Analytics analytics;
    @Inject @ForUser SharedPreferences preferences;
    @Inject SingleExpandingListViewListener singleExpandingListViewListener;
    @Inject LeaderboardCacheRx leaderboardCache;
    @Inject LeaderboardMarkUserListFragmentUtil fragmentUtil;

    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeContainer;

    private View mRankHeaderView;

    protected LeaderboardFilterFragment leaderboardFilterFragment;
    protected PerPagedLeaderboardKeyPreference savedPreference;

    protected PerPagedLeaderboardKey currentLeaderboardKey;
    protected LeaderboardType currentLeaderboardType = LeaderboardType.STOCKS;

    public static void putLeaderboardType(@NonNull Bundle args, @NonNull LeaderboardType leaderboardType)
    {
        args.putInt(BUNDLE_KEY_LEADERBOARD_TYPE_ID, leaderboardType.getLeaderboardTypeId());
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //please make sure to get the type before get key
        currentLeaderboardType = getInitialLeaderboardType();
        currentLeaderboardKey = getInitialLeaderboardKey();
        fragmentUtil.linkWith(this, currentLeaderboardType);
        setHasOptionsMenu(true);
    }

    protected PerPagedLeaderboardKey getInitialLeaderboardKey()
    {
        savedPreference = new PerPagedFilteredLeaderboardKeyPreference(
                getActivity(),
                preferences,
                PREFERENCE_KEY_PREFIX + leaderboardDefKey + "." + currentLeaderboardType.assetClass.name(),
                LeaderboardFilterSliderContainer.getStartingFilter(getResources(), leaderboardDefKey.key).getFilterStringSet());
        PerPagedFilteredLeaderboardKey initialKey = ((PerPagedFilteredLeaderboardKeyPreference) savedPreference)
                .getPerPagedFilteredLeaderboardKey();

        PerPagedFilteredLeaderboardKey filterKey = new PerPagedFilteredLeaderboardKey(initialKey, leaderboardDefKey.key, null, null);
        filterKey.setAssetClass(currentLeaderboardType.assetClass);
        return filterKey;
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
        inflateHeaderView();
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

    private void inflateHeaderView()
    {
        View userRankingHeaderView = inflateAndGetUserRankHeaderView();
        setupOwnRankingView(userRankingHeaderView);
        ((ListView) listView).addHeaderView(userRankingHeaderView);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                leaderboardCache.get(new PagedLeaderboardKey(leaderboardDefKey.key, 1));
            }
        });
    }

    @Override public void onStart()
    {
        super.onStart();
        fragmentUtil.onStart();
        onStopSubscriptions.add(((LeaderboardMarkUserListAdapter) itemViewAdapter).getFollowRequestedObservable()
                .subscribe(
                        fragmentUtil,
                        new TimberOnErrorAction("Error when receiving user follow requested")));
        requestDtos();
        fetchOwnRanking();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        MenuItem item = menu.findItem(R.id.button_leaderboard_filter);
        displayFilterIcon(item);
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
            newLeaderboardKey.setAssetClass(currentLeaderboardType.assetClass);
            leaderboardFilterFragment = null;
            Timber.d("%s", newLeaderboardKey.equals(currentLeaderboardKey));

            if (!newLeaderboardKey.equals(currentLeaderboardKey))
            {
                currentLeaderboardKey = newLeaderboardKey;
                swipeContainer.setRefreshing(true);
                itemViewAdapter.clear();
                unsubscribeListCache();
                requestDtos();
            }
        }
        else
        {
            Timber.d("onResume filterFragment is null");
        }
    }

    @Override public void onStop()
    {
        fragmentUtil.onStop();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        mRankHeaderView = null;
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

    @NonNull @Override public DTOCacheRx<PagedLeaderboardKey, LeaderboardMarkUserItemView.DTOList> getCache()
    {
        return new LeaderboardMarkUserItemViewDTOCacheRx(
                getResources(),
                currentUserId,
                leaderboardCache,
                userProfileCache);
    }

    @Override protected void linkWith(LeaderboardDefDTO leaderboardDefDTO)
    {
        super.linkWith(leaderboardDefDTO);
    }

    protected void saveCurrentFilterKey()
    {
        if (savedPreference != null)
        {
            savedPreference.set(currentLeaderboardKey);
        }
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
        if (mRankHeaderView instanceof LeaderboardMarkUserItemView)
        {
            LeaderboardMarkUserItemView ownRankingView = (LeaderboardMarkUserItemView) mRankHeaderView;
            ownRankingView.linkWith(getApplicablePortfolioId());
        }
    }

    protected void fetchOwnRanking()
    {
        updateLoadingCurrentRankHeaderView();
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                fetchOwnRankingInfoObservables())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<LeaderboardMarkUserItemView.Requisite>()
                        {
                            @Override public void call(LeaderboardMarkUserItemView.Requisite requisite)
                            {
                                updateCurrentRankHeaderView(requisite);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                updateCurrentRankHeaderView(null);
                            }
                        }));
    }

    @NonNull protected Observable<LeaderboardMarkUserItemView.Requisite> fetchOwnRankingInfoObservables()
    {
        UserOnLeaderboardKey userOnLeaderboardKey =
                new UserOnLeaderboardKey(
                        new LeaderboardKey(leaderboardDefKey.key, currentLeaderboardType != null ? currentLeaderboardType.assetClass : null),
                        currentUserId.toUserBaseKey());
        return Observable.zip(
                leaderboardCache.getOne(userOnLeaderboardKey),
                userProfileCache.getOne(currentUserId.toUserBaseKey()),
                new Func2<Pair<LeaderboardKey, LeaderboardDTO>, Pair<UserBaseKey, UserProfileDTO>, LeaderboardMarkUserItemView.Requisite>()
                {
                    @Override public LeaderboardMarkUserItemView.Requisite call(Pair<LeaderboardKey, LeaderboardDTO> currentLeaderboardPair,
                            Pair<UserBaseKey, UserProfileDTO> userProfilePair)
                    {
                        return new LeaderboardMarkUserItemView.Requisite(currentLeaderboardPair, userProfilePair);
                    }
                })
                .subscribeOn(Schedulers.computation());
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

    protected void updateCurrentRankHeaderView(@Nullable LeaderboardMarkUserItemView.Requisite requisite)
    {
        if (mRankHeaderView != null && mRankHeaderView instanceof LeaderboardMarkUserItemView)
        {
            LeaderboardMarkUserItemView leaderboardMarkUserItemView = (LeaderboardMarkUserItemView) mRankHeaderView;
            if (requisite == null || requisite.currentLeaderboardUserDTO == null)
            {
                leaderboardMarkUserItemView.displayUserIsNotRanked(requisite.currentUserProfileDTO);
                // user is not ranked, disable expandable view
                leaderboardMarkUserItemView.setOnClickListener(null);
            }
            else
            {
                leaderboardMarkUserItemView.display(new LeaderboardMarkUserOwnRankingView.DTO(
                        getResources(),
                        currentUserId,
                        requisite.currentLeaderboardUserDTO,
                        requisite.currentUserProfileDTO));
            }
        }
    }

    protected void setupOwnRankingView(View userRankingHeaderView)
    {
        if (userRankingHeaderView instanceof LeaderboardMarkUserItemView)
        {
            LeaderboardMarkUserItemView ownRankingView = (LeaderboardMarkUserItemView) userRankingHeaderView;
            if (ownRankingView.expandMark != null)
            {
                ownRankingView.expandMark.setVisibility(View.GONE);
            }
            if (ownRankingView.expandingLayout != null)
            {
                ownRankingView.expandingLayout.setVisibility(View.GONE);
                ownRankingView.setExpanded(false);
            }
        }
    }

    @Override protected void updateListViewRow(@NonNull final UserProfileDTO currentUserProfile, @NonNull final UserBaseKey heroId)
    {
        AdapterViewUtils.updateSingleRowWhere(
                listView,
                LeaderboardMarkUserItemView.DTO.class,
                new Predicate<LeaderboardMarkUserItemView.DTO>()
                {
                    @Override public boolean apply(LeaderboardMarkUserItemView.DTO dto)
                    {
                        boolean isUpdatedRow = dto.leaderboardUserDTO.getBaseKey().equals(heroId);
                        if (isUpdatedRow)
                        {
                            dto.followChanged(currentUserProfile, heroId);
                        }
                        return isUpdatedRow;
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

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        singleExpandingListViewListener.onItemClick(parent, view, position, id);
        super.onItemClick(parent, view, position, id);
    }
}
