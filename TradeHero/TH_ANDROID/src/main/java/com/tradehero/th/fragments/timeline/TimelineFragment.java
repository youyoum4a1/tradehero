package com.tradehero.th.fragments.timeline;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClickSticky;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTOList;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineSection;
import com.tradehero.th.api.timeline.key.TimelineKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.achievement.AchievementListFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinearDTOFactory;
import com.tradehero.th.fragments.discussion.DiscussionFragmentUtil;
import com.tradehero.th.fragments.portfolio.SimpleOwnPortfolioListItemAdapter;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.TabbedPositionListFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.watchlist.MainWatchlistPositionFragment;
import com.tradehero.th.models.discussion.UserDiscussionAction;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.persistence.level.LevelDefListCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.timeline.TimelineCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import timber.log.Timber;

abstract public class TimelineFragment extends DashboardFragment
{
    private static final String USER_BASE_KEY_BUNDLE_KEY = TimelineFragment.class.getName() + ".userBaseKey";

    //<editor-fold desc="Argument passing">
    public static void putUserBaseKey(@NonNull Bundle bundle, @NonNull UserBaseKey userBaseKey)
    {
        bundle.putBundle(USER_BASE_KEY_BUNDLE_KEY, userBaseKey.getArgs());
    }

    @Nullable protected static UserBaseKey getUserBaseKey(@NonNull Bundle args)
    {
        Bundle userBundle = args.getBundle(USER_BASE_KEY_BUNDLE_KEY);
        if (userBundle != null)
        {
            return new UserBaseKey(userBundle);
        }
        return null;
    }
    //</editor-fold>

    public enum TabType
    {
        TIMELINE, PORTFOLIO_LIST,
    }

    @Inject Analytics analytics;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject LevelDefListCacheRx levelDefListCache;
    @Inject Provider<DisplayablePortfolioFetchAssistant> displayablePortfolioFetchAssistantProvider;
    @Inject protected THRouter thRouter;
    @Inject protected TimelineCacheRx timelineCache;
    @Inject DiscussionFragmentUtil discussionFragmentUtil;
    @Inject AbstractDiscussionCompactItemViewLinearDTOFactory viewDTOFactory;
    @Inject CurrentUserId currentUserId;
    @Inject protected PortfolioCompactListCacheRx portfolioCompactListCache;

    @InjectView(R.id.timeline_list_view) StickyListHeadersListView timelineListView;
    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshContainer;

    protected UserBaseKey shownUserBaseKey;

    @Nullable protected UserProfileDTO shownProfile;
    private DisplayablePortfolioFetchAssistant displayablePortfolioFetchAssistant;
    private SimpleOwnPortfolioListItemAdapter portfolioListAdapter;
    private SubTimelineAdapterNew subTimelineAdapter;

    private UserProfileDetailView userProfileView;

    @NonNull public TabType currentTab = TabType.PORTFOLIO_LIST;
    protected boolean mIsOtherProfile = false;
    private boolean cancelRefreshingOnResume;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
        shownUserBaseKey = getShownUserBaseKey();
        if (shownUserBaseKey == null)
        {
            throw new IllegalArgumentException("Should not end up with null shownUserBaseKey");
        }
        portfolioListAdapter = new SimpleOwnPortfolioListItemAdapter(
                getActivity(),
                currentUserId.toUserBaseKey().equals(shownUserBaseKey),
                R.layout.portfolio_list_item,
                R.layout.timeline_list_item_loading,
                R.layout.timeline_list_item_spacing);
        portfolioListAdapter.setCurrentTabType(currentTab);
        //noinspection ArraysAsListWithZeroOrOneArgument
        portfolioListAdapter.setItems(Arrays.<Object>asList(SimpleOwnPortfolioListItemAdapter.DTO_LOADING));
        subTimelineAdapter = new SubTimelineAdapterNew(
                getActivity(),
                R.layout.timeline_item_view,
                R.layout.timeline_list_item_empty,
                R.layout.timeline_list_item_loading);
        subTimelineAdapter.setCurrentTabType(currentTab);
        //noinspection ArraysAsListWithZeroOrOneArgument
        subTimelineAdapter.appendHead(Arrays.asList(SubTimelineAdapterNew.DTO_LOADING));
    }

    @Nullable protected UserBaseKey getShownUserBaseKey()
    {
        return getUserBaseKey(getArguments());
    }

    @SuppressLint("InflateParams")
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        userProfileView = (UserProfileDetailView) inflater.inflate(R.layout.user_profile_detail_view, null);
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        timelineListView.addHeaderView(userProfileView, null, false);
        timelineListView.setAdapter(getAdapter());
        displayablePortfolioFetchAssistant = displayablePortfolioFetchAssistantProvider.get();
        registerButtonClicks();
        fetchLevelDefList();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        displayActionBarTitle();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onStart()
    {
        super.onStart();

        fetchShownUserProfile();
        fetchPortfolioList();

        registerUserDiscussionActions();

        onStopSubscriptions.add(
                Observable.merge(
                        subTimelineAdapter.getTabTypeObservable(),
                        portfolioListAdapter.getTabTypeObservable())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<TabType>()
                                {
                                    @Override public void call(TabType tabType)
                                    {
                                        display(tabType);
                                    }
                                },
                                new TimberOnErrorAction("Failed to display tabType")));
    }

    @Override public void onResume()
    {
        super.onResume();
        loadLatestTimeline();

        FlagNearEdgeScrollListener nearEndScrollListener = createNearEndScrollListener();
        nearEndScrollListener.lowerEndFlag();
        nearEndScrollListener.activateEnd();
        timelineListView.setOnScrollListener(new MultiScrollListener(fragmentElements.get().getListViewScrollListener(), nearEndScrollListener));
        swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                switch (currentTab)
                {
                    case TIMELINE:
                        loadLatestTimeline();
                        break;

                    case PORTFOLIO_LIST:
                        portfolioCompactListCache.invalidate(shownUserBaseKey);
                        portfolioCompactListCache.get(shownUserBaseKey);
                        userProfileCache.get().invalidate(shownUserBaseKey);
                        userProfileCache.get().get(shownUserBaseKey);
                        break;

                    default:
                        throw new IllegalArgumentException("Unhandled tabType " + currentTab);
                }
            }
        });

        if (cancelRefreshingOnResume)
        {
            swipeRefreshContainer.setRefreshing(false);
            cancelRefreshingOnResume = false;
        }
    }

    protected void loadLatestTimeline()
    {
        onStopSubscriptions.add(getTimelineObservable(new TimelineKey(TimelineSection.Timeline,
                shownUserBaseKey,
                subTimelineAdapter.getLatestRange()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<AbstractDiscussionCompactItemViewLinear.DTO>>()
                        {
                            @Override public void call(List<AbstractDiscussionCompactItemViewLinear.DTO> processed)
                            {
                                subTimelineAdapter.appendHead(subTimelineAdapter.reprocess(processed));
                                subTimelineAdapter.notifyDataSetChanged();
                                swipeRefreshContainer.setRefreshing(false);
                            }
                        },
                        new ToastOnErrorAction()));
    }

    protected void loadOlderTimeline()
    {
        subTimelineAdapter.appendTail(Arrays.asList(SubTimelineAdapterNew.DTO_LOADING));
        subTimelineAdapter.notifyDataSetChanged();
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                getTimelineObservable(new TimelineKey(TimelineSection.Timeline,
                        shownUserBaseKey,
                        subTimelineAdapter.getOlderRange())))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<AbstractDiscussionCompactItemViewLinear.DTO>>()
                        {
                            @Override public void call(List<AbstractDiscussionCompactItemViewLinear.DTO> processed)
                            {
                                subTimelineAdapter.appendTail(subTimelineAdapter.reprocess(processed));
                                subTimelineAdapter.notifyDataSetChanged();
                            }
                        },
                        new ToastOnErrorAction()));
    }

    @NonNull protected Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>> getTimelineObservable(
            @NonNull TimelineKey key)
    {
        return timelineCache.get(key)
                .subscribeOn(Schedulers.computation())
                .take(1)
                .flatMap(new Func1<Pair<TimelineKey, TimelineDTO>, Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>>>()
                {
                    @Override public Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>> call(
                            Pair<TimelineKey, TimelineDTO> pair)
                    {
                        List<TimelineItemDTO> enhancedItems = pair.second.getEnhancedItems();
                        if (enhancedItems != null)
                        {
                            return viewDTOFactory.createTimelineItemViewLinearDTOs(enhancedItems);
                        }
                        return Observable.<List<AbstractDiscussionCompactItemViewLinear.DTO>>just(
                                new ArrayList<AbstractDiscussionCompactItemViewLinear.DTO>());
                    }
                });
    }

    private FlagNearEdgeScrollListener createNearEndScrollListener()
    {
        return new FlagNearEdgeScrollListener()
        {
            @Override public void raiseEndFlag()
            {
                super.raiseEndFlag();
                loadOlderTimeline();
            }
        };
    }

    @Override public void onPause()
    {
        fragmentElements.get().getMovableBottom().setOnMovableBottomTranslateListener(null);
        timelineListView.setOnScrollListener(null);
        swipeRefreshContainer.setOnRefreshListener(null);
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        displayablePortfolioFetchAssistant = null;
        this.userProfileView = null;

        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        portfolioListAdapter = null;
        subTimelineAdapter = null;
        super.onDestroy();
    }

    @NonNull protected StickyListHeadersAdapter getAdapter()
    {
        switch (currentTab)
        {
            case PORTFOLIO_LIST:
                return portfolioListAdapter;
            case TIMELINE:
                return subTimelineAdapter;
            default:
                throw new IllegalArgumentException("Unhandled TabType." + currentTab);
        }
    }

    protected void registerButtonClicks()
    {
        onDestroyViewSubscriptions.add(userProfileView.getButtonClickedObservable()
                .subscribe(
                        new Action1<UserProfileCompactViewHolder.ButtonType>()
                        {
                            @Override public void call(UserProfileCompactViewHolder.ButtonType buttonType)
                            {
                                handleButtonClicked(buttonType);
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to register to button clicks")));
    }

    //<editor-fold desc="Display methods">
    private void fetchPortfolioList()
    {
        onStopSubscriptions.add(displayablePortfolioFetchAssistant.get(shownUserBaseKey)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DisplayablePortfolioDTOList>()
                {
                    @Override public void onCompleted()
                    {
                        Timber.d("completed");
                    }

                    @Override public void onError(Throwable e)
                    {
                        Timber.e(e, "error");
                    }

                    @Override public void onNext(DisplayablePortfolioDTOList displayablePortfolioDTOs)
                    {
                        swipeRefreshContainer.setRefreshing(false);
                        cancelRefreshingOnResume = true;
                        portfolioListAdapter.setItems((List) displayablePortfolioDTOs);
                        portfolioListAdapter.notifyDataSetChanged();
                    }
                }));
    }

    protected void linkWith(@NonNull UserProfileDTO userProfileDTO)
    {
        this.shownProfile = userProfileDTO;
        userProfileView.display(shownProfile);
        displayActionBarTitle();
    }

    protected void display(@NonNull TabType tabType)
    {
        currentTab = tabType;
        timelineListView.setAdapter(getAdapter());
        portfolioListAdapter.setCurrentTabType(tabType);
        portfolioListAdapter.notifyDataSetChanged();
        subTimelineAdapter.setCurrentTabType(tabType);
        subTimelineAdapter.notifyDataSetChanged();
    }
    //</editor-fold>

    protected void displayActionBarTitle()
    {
        if (shownProfile != null)
        {
            if (shownProfile.id == currentUserId.get())
            {
                setActionBarTitle(getString(R.string.me));
            }
            else
            {
                setActionBarTitle(UserBaseDTOUtil.getLongDisplayName(getResources(), shownProfile));
            }
        }
        else
        {
            setActionBarTitle(R.string.loading_loading);
        }
    }

    protected void fetchShownUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(this, userProfileCache.get().get(shownUserBaseKey))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new TimelineFragmentUserProfileCacheObserver()));
    }

    protected void fetchLevelDefList()
    {
        onDestroyViewSubscriptions.add(AppObservable.bindFragment(this, levelDefListCache.getOne(new LevelDefListId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<LevelDefListId, LevelDefDTOList>>()
                        {
                            @Override public void call(Pair<LevelDefListId, LevelDefDTOList> pair)
                            {
                                userProfileView.setLevelDef(pair.second);
                            }
                        },
                        new TimberOnErrorAction("Failed to fetch level definitions")));
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClickSticky(R.id.timeline_list_view)
    protected void onMainItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Object item = adapterView.getItemAtPosition(i);
        if (item instanceof DisplayablePortfolioDTO)
        {
            DisplayablePortfolioDTO displayablePortfolioDTO = (DisplayablePortfolioDTO) item;
            if (displayablePortfolioDTO.portfolioDTO != null)
            {
                if (displayablePortfolioDTO.portfolioDTO.isWatchlist)
                {
                    pushWatchlistPositionFragment();
                }
                else
                {
                    pushPositionListFragment(displayablePortfolioDTO.ownedPortfolioId, displayablePortfolioDTO.portfolioDTO);
                }
            }
        }
        else if (item.equals(SubTimelineAdapterNew.DTO_CALL_ACTION))
        {
            navigator.get().goToTab(RootFragmentType.TRENDING);
        }
        else
        {
            Timber.d("TimelineFragment, unhandled view %s", view);
        }
    }

    private void pushPositionListFragment(OwnedPortfolioId ownedPortfolioId, @Nullable PortfolioDTO portfolioDTO)
    {
        Bundle args = new Bundle();

        TabbedPositionListFragment.putApplicablePortfolioId(args, ownedPortfolioId);
        TabbedPositionListFragment.putGetPositionsDTOKey(args, ownedPortfolioId);
        TabbedPositionListFragment.putShownUser(args, ownedPortfolioId.getUserBaseKey());

        if (portfolioDTO != null)
        {
            TabbedPositionListFragment.putIsFX(args, portfolioDTO.assetClass);
            if (portfolioDTO.providerId != null && portfolioDTO.providerId > 0)
            {
                TabbedPositionListFragment.putProviderId(args, new ProviderId(portfolioDTO.providerId));
                navigator.get().pushFragment(CompetitionLeaderboardPositionListFragment.class, args);
                return;
            }
        }
        navigator.get().pushFragment(TabbedPositionListFragment.class, args);
    }

    private void pushWatchlistPositionFragment()
    {
        Bundle args = new Bundle();
        MainWatchlistPositionFragment.putShowActionBarTitle(args, true);
        navigator.get().pushFragment(MainWatchlistPositionFragment.class, args);
    }

    protected class TimelineFragmentUserProfileCacheObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            linkWith(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(getString(R.string.error_fetch_user_profile));
        }
    }

    protected int getFollowType()
    {
        UserProfileDTO userProfileDTO =
                userProfileCache.get().getCachedValue(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            UserBaseKey purchaserKey = currentUserId.toUserBaseKey();
            if (purchaserKey != null)
            {
                UserProfileDTO purchaserProfile = userProfileCache.get().getCachedValue(purchaserKey);
                if (purchaserProfile != null)
                {
                    return purchaserProfile.getFollowType(shownUserBaseKey);
                }
            }
        }
        return 0;
    }

    protected void registerUserDiscussionActions()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                subTimelineAdapter.getUserActionObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<UserDiscussionAction, Observable<UserDiscussionAction>>()
                {
                    @Override public Observable<UserDiscussionAction> call(UserDiscussionAction userDiscussionAction)
                    {
                        return discussionFragmentUtil.handleUserAction(getActivity(), userDiscussionAction);
                    }
                })
                .retry()
                .subscribe(
                        new Action1<UserDiscussionAction>()
                        {
                            @Override public void call(UserDiscussionAction userDiscussionAction)
                            {
                                Timber.e(new Exception("Not handled " + userDiscussionAction), "");
                            }
                        },
                        new TimberOnErrorAction("When registering user actions")));
    }

    //<editor-fold desc="UserProfileCompactViewHolder">
    protected void handleButtonClicked(@NonNull UserProfileCompactViewHolder.ButtonType buttonType)
    {
        switch (buttonType)
        {
            case HEROES:
                pushHeroFragment();
                break;

            case FOLLOWERS:
                pushFollowerFragment();
                break;

            case ACHIEVEMENTS:
                pushAchievementFragment();
                break;

            default:
                throw new IllegalArgumentException("Unhandled ButtonType." + buttonType);
        }
    }

    protected void pushHeroFragment()
    {
        Bundle bundle = new Bundle();
        HeroManagerFragment.putFollowerId(
                bundle,
                mIsOtherProfile ? shownUserBaseKey : currentUserId.toUserBaseKey());
        navigator.get().pushFragment(HeroManagerFragment.class, bundle);
    }

    protected void pushFollowerFragment()
    {
        Bundle bundle = new Bundle();
        FollowerManagerFragment.putHeroId(
                bundle,
                mIsOtherProfile ? shownUserBaseKey : currentUserId.toUserBaseKey());
        navigator.get().pushFragment(FollowerManagerFragment.class, bundle);
    }

    protected void pushAchievementFragment()
    {
        Bundle bundle = new Bundle();
        AchievementListFragment.putUserId(bundle, mIsOtherProfile ? shownUserBaseKey : currentUserId.toUserBaseKey());
        navigator.get().pushFragment(AchievementListFragment.class, bundle);
    }
    //</editor-fold>
}
