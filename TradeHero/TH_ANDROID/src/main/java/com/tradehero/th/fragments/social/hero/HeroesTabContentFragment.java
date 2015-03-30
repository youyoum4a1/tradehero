package com.tradehero.th.fragments.social.hero;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.billing.purchase.PurchaseResult;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.social.FragmentUtils;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCacheRx;
import com.tradehero.th.persistence.social.HeroListCacheRx;
import com.tradehero.th.persistence.social.HeroType;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

abstract public class HeroesTabContentFragment extends DashboardFragment
        implements SwipeRefreshLayout.OnRefreshListener
{
    private static final String BUNDLE_KEY_FOLLOWER_ID =
            HeroesTabContentFragment.class.getName() + ".followerId";

    private HeroListItemAdapter heroListAdapter;
    // The follower whose heroes we are listing
    @NonNull private UserBaseKey followerId;

    @Inject HeroListCacheRx heroListCache;
    /** when no heroes */
    @Inject Lazy<LeaderboardDefCacheRx> leaderboardDefCache;
    @Inject CurrentUserId currentUserId;
    @Inject THRouter thRouter;

    @InjectView(android.R.id.progress) public ProgressBar progressBar;
    @InjectView(R.id.heros_list) public ListView heroListView;
    @InjectView(R.id.swipe_to_refresh_layout) public SwipeRefreshLayout swipeRefreshLayout;
    @Inject protected THBillingInteractorRx userInteractorRx;

    //<editor-fold desc="Argument Passing">
    public static void putFollowerId(Bundle args, UserBaseKey followerId)
    {
        args.putBundle(BUNDLE_KEY_FOLLOWER_ID, followerId.getArgs());
    }

    @NonNull public static UserBaseKey getFollowerId(@NonNull Bundle args)
    {
        return new UserBaseKey(args.getBundle(BUNDLE_KEY_FOLLOWER_ID));
    }
    //</editor-fold>

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.followerId = getFollowerId(getArguments());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_store_manage_heroes, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        this.heroListAdapter = new HeroListItemAdapter(
                getActivity(),
                /**R.layout.hero_list_item_empty_placeholder*/getEmptyViewLayout(),
                R.layout.hero_list_item,
                R.layout.hero_list_header,
                R.layout.hero_list_header);
        this.heroListAdapter.setFollowerId(followerId);
        if (this.swipeRefreshLayout != null)
        {
            this.swipeRefreshLayout.setOnRefreshListener(this);
        }
        if (this.heroListView != null)
        {
            this.heroListView.setAdapter(this.heroListAdapter);
            this.heroListView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener()
                    {
                        @Override public void onItemClick(AdapterView<?> parent, View view1, int position, long id)
                        {
                            HeroesTabContentFragment.this.handleHeroClicked(parent, view1, position, id);
                        }
                    }
            );
        }
        setListShown(false);
        this.heroListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
    }

    private void setListShown(boolean shown)
    {
        if (shown)
        {
            this.heroListView.setVisibility(View.VISIBLE);
            this.progressBar.setVisibility(View.INVISIBLE);
        }
        else
        {
            this.heroListView.setVisibility(View.INVISIBLE);
            this.progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(getTitle());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onStart()
    {
        super.onStart();
        onStopSubscriptions.add(heroListAdapter.getUserActionObservable()
                .subscribe(
                        new Action1<HeroListItemView.UserAction>()
                        {
                            @Override public void call(HeroListItemView.UserAction userAction)
                            {
                                handleUserAction(userAction);
                            }
                        },
                        new EmptyAction1<Throwable>()));
    }

    @Override public void onResume()
    {
        super.onResume();
        enableSwipeRefresh(false);
        displayProgress(true);
        fetchHeroes();
    }

    private boolean isCurrentUser()
    {
        UserBaseKey followerId = getFollowerId(getArguments());
        return currentUserId != null && (followerId.key.intValue() == currentUserId.toUserBaseKey().key.intValue());
    }

    @LayoutRes private int getEmptyViewLayout()
    {
        if (isCurrentUser())
        {
            return R.layout.hero_list_item_empty_placeholder;
        }
        else
        {
            return R.layout.hero_list_item_empty_placeholder_for_other;
        }
    }

    private int getTitle()
    {
        if (isCurrentUser())
        {
            return R.string.manage_my_heroes_title;
        }
        else
        {
            return R.string.manage_heroes_title;
        }
    }

    private void refreshContent()
    {
        fetchHeroes();
    }

    protected HeroTypeResourceDTO getHeroTypeResource()
    {
        return HeroTypeResourceDTOFactory.create(getHeroType());
    }

    @NonNull abstract protected HeroType getHeroType();

    @Override public void onDestroyView()
    {
        this.heroListAdapter = null;
        if (this.heroListView != null)
        {
            this.heroListView.setOnItemClickListener(null);
            this.heroListView.setOnScrollListener(null);
        }
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    protected void fetchHeroes()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                heroListCache.get(followerId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HeroManagerHeroListCacheObserver()));
    }

    private void handleHeroClicked(AdapterView<?> parent, View view, int position, long id)
    {
        pushTimelineFragment(((HeroDTO) parent.getItemAtPosition(position)).getBaseKey());
    }

    protected void unfollow(@NonNull UserBaseKey userBaseKey)
    {
        onStopSubscriptions.add(
                AppObservable.bindFragment(
                        this,
                        new SimpleFollowUserAssistant(getActivity(), userBaseKey)
                                .launchUnFollowRx())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<UserProfileDTO>()
                                {
                                    @Override public void call(UserProfileDTO profile)
                                    {
                                        HeroesTabContentFragment.this.fetchHeroes();
                                    }
                                },
                                new ToastOnErrorAction()));
    }

    private void pushTimelineFragment(UserBaseKey userBaseKey)
    {
        Bundle args = new Bundle();
        thRouter.save(args, userBaseKey);
        navigator.get().pushFragment(PushableTimelineFragment.class, args);
    }

    protected void handleUserAction(@NonNull HeroListItemView.UserAction userAction)
    {
        if (userAction instanceof HeroListItemAdapter.UserActionMostSkilled)
        {
            handleGoMostSkilled();
        }
        else if (userAction instanceof HeroListItemView.UserActionDelete)
        {
            handleHeroStatusChangeRequired(((HeroListItemView.UserActionDelete) userAction).heroDTO);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled userAction " + userAction);
        }
    }

    private void handleGoMostSkilled()
    {
        // TODO this feels HACKy
        //navigator.popFragment();

        // TODO make it go to most skilled
        //navigator.goToTab(DashboardTabType.COMMUNITY);

        LeaderboardDefKey key =
                new LeaderboardDefKey(LeaderboardDefKeyKnowledge.MOST_SKILLED_ID);
        LeaderboardDefDTO dto = leaderboardDefCache.get().getCachedValue(key);
        Bundle bundle = new Bundle(getArguments());
        if (dto != null)
        {
            LeaderboardMarkUserListFragment.putLeaderboardDefKey(bundle, dto.getLeaderboardDefKey());
        }
        else
        {
            LeaderboardMarkUserListFragment.putLeaderboardDefKey(bundle, new LeaderboardDefKey(LeaderboardDefKeyKnowledge.MOST_SKILLED_ID));
        }
        navigator.get().pushFragment(LeaderboardMarkUserListFragment.class, bundle);
    }

    private void handleHeroStatusChangeRequired(@NonNull final HeroDTO clickedHeroDTO)
    {
        if (!clickedHeroDTO.active)
        {
            //noinspection unchecked
            onStopSubscriptions.add(AppObservable.bindFragment(
                    this,
                    HeroAlertDialogRxUtil.popAlertFollowHero(getActivity()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Func1<OnDialogClickEvent, Observable<PurchaseResult>>()
                    {
                        @Override public Observable<PurchaseResult> call(OnDialogClickEvent event)
                        {
                            if (event.isPositive())
                            {
                                //noinspection unchecked
                                return userInteractorRx.purchaseAndPremiumFollowAndClear(clickedHeroDTO.getBaseKey());
                            }
                            return Observable.empty();
                        }
                    })
                    .subscribe(
                            new Action1<PurchaseResult>()
                            {
                                @Override public void call(PurchaseResult result)
                                {
                                    Timber.d("onUserFollowSuccess");
                                    THToast.show(HeroesTabContentFragment.this.getString(R.string.manage_heroes_unfollow_success));
                                    HeroesTabContentFragment.this.fetchHeroes();
                                }
                            },
                            new Action1<Throwable>()
                            {
                                @Override public void call(Throwable error)
                                {
                                    //TODO offical accounts, do not unfollow
                                    if (clickedHeroDTO.isOfficialAccount())
                                    {
                                        THToast.show(
                                                HeroesTabContentFragment.this.getString(R.string.manage_heroes_unfollow_official_accounts_failed));
                                    }
                                    else
                                    {
                                        Timber.e(error, "onUserFollowFailed error");
                                        THToast.show(HeroesTabContentFragment.this.getString(R.string.manage_heroes_unfollow_failed));
                                    }
                                }
                            }
                    ));
        }
        else
        {
            onStopSubscriptions.add(HeroAlertDialogRxUtil.popAlertUnFollowHero(getActivity())
                    .subscribe(
                            new Action1<OnDialogClickEvent>()
                            {
                                @Override public void call(OnDialogClickEvent event)
                                {
                                    if (event.isPositive())
                                    {
                                        THToast.show(HeroesTabContentFragment.this.getString(R.string.manage_heroes_unfollow_progress_message));
                                        HeroesTabContentFragment.this.unfollow(clickedHeroDTO.getBaseKey());
                                    }
                                }
                            },
                            new ToastOnErrorAction()
                    ));
        }
    }

    abstract protected void display(HeroDTOExtWrapper heroDTOExtWrapper);

    protected void display(List<HeroDTO> heroDTOs)
    {
        linkWith(heroDTOs);
    }

    public void linkWith(List<HeroDTO> heroDTOs)
    {
        heroListAdapter.setItems(heroDTOs);
        displayHeroList();
    }

    public void display()
    {
        displayHeroList();
    }

    @Override public void onRefresh()
    {
        refreshContent();
    }

    private void onRefreshCompleted()
    {
        if (swipeRefreshLayout != null)
        {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void enableSwipeRefresh(boolean enable)
    {
        if (swipeRefreshLayout != null)
        {
            if (!enable)
            {
                swipeRefreshLayout.setEnabled(false);
            }
            else
            {
                swipeRefreshLayout.setEnabled(true);
            }
        }
    }

    private void displayHeroList()
    {
        if (heroListAdapter != null)
        {
            heroListAdapter.notifyDataSetChanged();
        }
    }

    private void displayProgress(boolean running)
    {
        if (progressBar != null)
        {
            progressBar.setVisibility(running ? View.VISIBLE : View.GONE);
        }
    }

    private class HeroManagerHeroListCacheObserver
            implements Observer<Pair<UserBaseKey, HeroDTOExtWrapper>>
    {
        @Override public void onNext(Pair<UserBaseKey, HeroDTOExtWrapper> pair)
        {
            //displayProgress(false);
            onRefreshCompleted();
            setListShown(true);
            display(pair.second);
            enableSwipeRefresh(true);
            notifyHeroesLoaded(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            displayProgress(false);
            setListShown(true);
            enableSwipeRefresh(true);
            Timber.e(e, "Could not fetch heroes");
            THToast.show(R.string.error_fetch_hero);
        }
    }

    private void notifyHeroesLoaded(HeroDTOExtWrapper value)
    {
        OnHeroesLoadedListener listener =
                FragmentUtils.getParent(this, OnHeroesLoadedListener.class);
        if (listener != null && !isDetached())
        {
            listener.onHerosLoaded(getHeroTypeResource(), value);
        }
    }
}