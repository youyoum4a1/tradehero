package com.tradehero.th.fragments.leaderboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
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
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.tradehero.th.persistence.leaderboard.LeaderboardCacheRx;
import com.tradehero.th.persistence.leaderboard.PerPagedFilteredLeaderboardKeyPreference;
import com.tradehero.th.persistence.leaderboard.PerPagedLeaderboardKeyPreference;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.LiveWidgetScrollListener;
import com.tradehero.th.widget.MultiRecyclerScrollListener;
import com.tradehero.th.widget.list.SingleExpandingListViewListener;
import javax.inject.Inject;
import retrofit.RetrofitError;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class LeaderboardMarkUserRecyclerFragment extends BaseLeaderboardPagedRecyclerRxFragment<
        PagedLeaderboardKey,
        LeaderboardItemDisplayDTO,
        LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>,
        LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>>
{
    public static final String PREFERENCE_KEY_PREFIX = LeaderboardMarkUserRecyclerFragment.class.getName();
    private static final String BUNDLE_KEY_LEADERBOARD_TYPE_ID = LeaderboardMarkUserRecyclerFragment.class.getName() + ".leaderboardTypeId";

    @Inject Analytics analytics;
    @Inject @ForUser SharedPreferences preferences;
    @Inject SingleExpandingListViewListener singleExpandingListViewListener;
    @Inject LeaderboardCacheRx leaderboardCache;
    @Inject LeaderboardMarkUserListFragmentUtil fragmentUtil;

    protected LeaderboardFilterFragment leaderboardFilterFragment;
    protected PerPagedLeaderboardKeyPreference savedPreference;

    protected PerPagedLeaderboardKey currentLeaderboardKey;
    protected LeaderboardType currentLeaderboardType = LeaderboardType.STOCKS;
    private LeaderboardMarkedUserItemDisplayDto.Requisite ownRankRequisite;

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

        PerPagedFilteredLeaderboardKey filterKey = new PerPagedFilteredLeaderboardKey(initialKey, leaderboardDefKey.key, null, perPage);
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
        return inflater.inflate(R.layout.leaderboard_mark_user_recyclerview, container, false);
    }

    @Override public void onStart()
    {
        super.onStart();
        fragmentUtil.onStart();
        onStopSubscriptions.add(((LeaderboardMarkUserRecyclerAdapter<LeaderboardItemDisplayDTO>) itemViewAdapter).getUserActionObservable()
                .subscribe(
                        fragmentUtil,
                        new TimberOnErrorAction1("Error when receiving user follow requested")));
        if ((itemViewAdapter != null) && (itemViewAdapter.getItemCount() == 0))
        {
            requestDtos();
        }
        fetchOwnRanking();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
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

    @Override public void onDestroy()
    {
        this.leaderboardFilterFragment = null;
        ownRankRequisite = null;
        saveCurrentFilterKey();
        fragmentUtil.onDestroy();
        super.onDestroy();
    }

    @Override public void updateRow(LeaderboardItemDisplayDTO dto)
    {
        int position = itemViewAdapter.indexOf(dto);
        if (position >= 0)
        {
            itemViewAdapter.notifyItemChanged(position);
        }
    }

    @NonNull protected LeaderboardMarkUserRecyclerAdapter<LeaderboardItemDisplayDTO> createItemViewAdapter()
    {
        LeaderboardMarkUserRecyclerAdapter<LeaderboardItemDisplayDTO> adapter = new LeaderboardMarkUserRecyclerAdapter<>(
                LeaderboardItemDisplayDTO.class,
                getActivity(),
                R.layout.lbmu_item_roi_mode,
                R.layout.lbmu_item_own_ranking_roi_mode,
                new LeaderboardKey(leaderboardDefKey.key));
        adapter.setApplicablePortfolioId(getApplicablePortfolioId());
        return adapter;
    }

    @NonNull @Override protected RecyclerView.OnScrollListener createRecyclerViewScrollListener()
    {
        return new MultiRecyclerScrollListener(
                super.createRecyclerViewScrollListener(),
                new RecyclerView.OnScrollListener()
                {
                    private boolean scrollStateChanged;

                    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                    {
                        if (scrollStateChanged)
                        {
                            LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                            int visibleItemCount = mLayoutManager.getChildCount();
                            int totalItemCount = mLayoutManager.getItemCount();
                            int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                            if ((totalItemCount - visibleItemCount) < (firstVisibleItem + 1))
                            {
                                scrollStateChanged = false;
                                requestDtos();
                            }
                        }

                        if (getParentFragment() instanceof LeaderboardCommunityFragment)
                        {
                            ((LeaderboardCommunityFragment) getParentFragment()).getLiveFragmentUtil()
                                    .setLiveWidgetTranslationY(fragmentElements.get().getMovableBottom().getTranslationY());
                        }
                    }

                    @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState)
                    {
                        scrollStateChanged = true;
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

    @NonNull @Override public DTOCacheRx<PagedLeaderboardKey, LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>> getCache()
    {
        return new LeaderboardMarkUserItemViewDTOCacheRx(
                getResources(),
                currentUserId,
                leaderboardCache,
                userProfileCache);
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
            ((LeaderboardMarkUserRecyclerAdapter) itemViewAdapter).setApplicablePortfolioId(purchaseApplicablePortfolioId);
        }
    }

    protected void fetchOwnRanking()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                fetchOwnRankingInfoObservables())
                .startWith(ownRankRequisite)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<LeaderboardMarkedUserItemDisplayDto.Requisite>()
                        {
                            @Override public void call(@Nullable LeaderboardMarkedUserItemDisplayDto.Requisite requisite)
                            {
                                updateCurrentRankView(requisite);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                updateCurrentRankView(null);
                            }
                        }));
    }

    @NonNull protected Observable<LeaderboardMarkedUserItemDisplayDto.Requisite> fetchOwnRankingInfoObservables()
    {
        UserOnLeaderboardKey userOnLeaderboardKey =
                new UserOnLeaderboardKey(
                        new LeaderboardKey(leaderboardDefKey.key, currentLeaderboardType != null ? currentLeaderboardType.assetClass : null),
                        currentUserId.toUserBaseKey());
        return Observable.zip(
                leaderboardCache.getOne(userOnLeaderboardKey),
                userProfileCache.getOne(currentUserId.toUserBaseKey()),
                new Func2<Pair<LeaderboardKey, LeaderboardDTO>, Pair<UserBaseKey, UserProfileDTO>, LeaderboardMarkedUserItemDisplayDto.Requisite>()
                {
                    @Override public LeaderboardMarkedUserItemDisplayDto.Requisite call(Pair<LeaderboardKey, LeaderboardDTO> currentLeaderboardPair,
                            Pair<UserBaseKey, UserProfileDTO> userProfilePair)
                    {
                        return new LeaderboardMarkedUserItemDisplayDto.Requisite(currentLeaderboardPair, userProfilePair);
                    }
                })
                .subscribeOn(Schedulers.computation());
    }

    protected void updateCurrentRankView(@Nullable LeaderboardMarkedUserItemDisplayDto.Requisite requisite)
    {
        LeaderboardMarkedUserItemDisplayDto ownRankingDto;
        if (requisite == null)
        {
            //Is Loading
            ownRankingDto = createLoadingOwnRankingDTO();
        }
        else if (requisite.currentLeaderboardUserDTO == null)
        {
            //Not Ranked
            ownRankingDto = createNotRankedOwnRankingDTO(requisite);
        }
        else
        {
            ownRankingDto = createRankedOwnRankingDTO(requisite);
        }
        itemViewAdapter.add(ownRankingDto);
    }

    protected LeaderboardMarkedUserItemDisplayDto createLoadingOwnRankingDTO()
    {
        return new LeaderboardMarkedUserItemDisplayDto(getResources(), currentUserId);
    }

    protected LeaderboardMarkedUserItemDisplayDto createNotRankedOwnRankingDTO(LeaderboardMarkedUserItemDisplayDto.Requisite requisite)
    {
        return new LeaderboardMarkedUserItemDisplayDto(getResources(), currentUserId,
                requisite.currentUserProfileDTO);
    }

    protected LeaderboardMarkedUserItemDisplayDto createRankedOwnRankingDTO(LeaderboardMarkedUserItemDisplayDto.Requisite requisite)
    {
        LeaderboardMarkedUserItemDisplayDto dto = new LeaderboardMarkedUserItemDisplayDto(
                getResources(),
                currentUserId,
                requisite.currentLeaderboardUserDTO,
                requisite.currentUserProfileDTO);
        dto.setIsMyOwnRanking(true);
        return dto;
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

    @Override protected Pair<PagedLeaderboardKey, LeaderboardMarkedUserItemDisplayDto.DTOList<LeaderboardItemDisplayDTO>> onMap(
            Pair<PagedLeaderboardKey, LeaderboardMarkedUserItemDisplayDto.DTOList<LeaderboardItemDisplayDTO>> receivedPair)
    {
        int page = receivedPair.first.page == null ? FIRST_PAGE : receivedPair.first.page;
        int rank = (page - FIRST_PAGE) * perPage;
        for (LeaderboardItemDisplayDTO dto : receivedPair.second)
        {
            rank++;
            dto.setRanking(rank);
        }
        return super.onMap(receivedPair);
    }

    @Override protected void onNext(@NonNull PagedLeaderboardKey key, @NonNull LeaderboardMarkedUserItemDisplayDto.DTOList value)
    {
        super.onNext(key, value);
        //swipeContainer.setRefreshing(false);
    }

    @Override protected void onError(@NonNull PagedLeaderboardKey key, @NonNull Throwable error)
    {
        super.onError(key, error);
        if (!(error instanceof RetrofitError) || ((RetrofitError) error).getResponse() == null
                || ((RetrofitError) error).getResponse().getStatus() != 404)
        {
            Timber.e(error, "Failed fetching leaderboard");
        }
        THToast.show(R.string.error_fetch_leaderboard_info);
        //swipeContainer.setRefreshing(false);
    }
}
