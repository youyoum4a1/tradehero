package com.tradehero.th.fragments.social.hero;

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
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.social.FragmentUtils;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCacheRx;
import com.tradehero.th.persistence.social.HeroListCacheRx;
import com.tradehero.th.persistence.social.HeroType;
import com.tradehero.th.rx.dialog.AlertDialogButtonConstants;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

abstract public class HeroesTabContentFragment extends BasePurchaseManagerFragment
        implements SwipeRefreshLayout.OnRefreshListener
{
    private static final String BUNDLE_KEY_FOLLOWER_ID =
            HeroesTabContentFragment.class.getName() + ".followerId";

    private HeroListItemAdapter heroListAdapter;
    // The follower whose heroes we are listing
    @NonNull private UserBaseKey followerId;

    @Inject HeroListCacheRx heroListCache;
    @Inject public HeroAlertDialogRxUtil heroAlertDialogRxUtil;
    /** when no heroes */
    @Inject Lazy<LeaderboardDefCacheRx> leaderboardDefCache;
    @Inject HeroTypeResourceDTOFactory heroTypeResourceDTOFactory;
    @Inject CurrentUserId currentUserId;
    @Inject THRouter thRouter;

    @InjectView(android.R.id.progress) public ProgressBar progressBar;
    @InjectView(R.id.heros_list) public ListView heroListView;
    @InjectView(R.id.swipe_to_refresh_layout) public SwipeRefreshLayout swipeRefreshLayout;
    @NonNull private SubscriptionList subscriptions;

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
        this.subscriptions = new SubscriptionList();
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
        this.heroListAdapter.setHeroStatusButtonClickedListener(createHeroStatusButtonClickedListener());
        this.heroListAdapter.setFollowerId(followerId);
        this.heroListAdapter.setMostSkilledClicked(createHeroListMostSkilledClickedListener());
        if (this.swipeRefreshLayout != null)
        {
            this.swipeRefreshLayout.setOnRefreshListener(this);
        }
        if (this.heroListView != null)
        {
            this.heroListView.setAdapter(this.heroListAdapter);
            this.heroListView.setOnItemClickListener(
                    this::handleHeroClicked
            );
        }
        setListShown(false);
        this.heroListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
    }

    protected HeroListItemView.OnHeroStatusButtonClickedListener createHeroStatusButtonClickedListener()
    {
        return (heroListItemView, heroDTO) -> handleHeroStatusButtonClicked(heroDTO);
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

    private int getEmptyViewLayout()
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
        return heroTypeResourceDTOFactory.create(getHeroType());
    }

    abstract protected HeroType getHeroType();

    @Override public void onStop()
    {
        subscriptions.unsubscribe();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        if (this.heroListAdapter != null)
        {
            this.heroListAdapter.setHeroStatusButtonClickedListener(null);
            this.heroListAdapter.setMostSkilledClicked(null);
        }
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
        subscriptions.add(AndroidObservable.bindFragment(
                this,
                heroListCache.get(followerId))
                .subscribe(new HeroManagerHeroListCacheObserver()));
    }

    private void handleHeroStatusButtonClicked(HeroDTO heroDTO)
    {
        handleHeroStatusChangeRequired(heroDTO);
    }

    private void handleHeroClicked(AdapterView<?> parent, View view, int position, long id)
    {
        pushTimelineFragment(((HeroDTO) parent.getItemAtPosition(position)).getBaseKey());
    }

    private void handleHeroStatusChangeRequired(@NonNull final HeroDTO clickedHeroDTO)
    {
        if (!clickedHeroDTO.active)
        {
            //noinspection unchecked
            subscriptions.add(AndroidObservable.bindFragment(
                    this,
                    heroAlertDialogRxUtil.popAlertFollowHero(getActivity()))
                    .filter(pair -> pair.second.equals(AlertDialogButtonConstants.POSITIVE_BUTTON_INDEX))
                    .flatMap(pair -> userInteractorRx.purchaseAndPremiumFollowAndClear(clickedHeroDTO.getBaseKey()))
                    .subscribe(
                            result -> {
                                Timber.d("onUserFollowSuccess");
                                THToast.show(getString(R.string.manage_heroes_unfollow_success));
                                fetchHeroes();
                            },
                            error -> {
                                //TODO offical accounts, do not unfollow
                                if (clickedHeroDTO.isOfficialAccount())
                                {
                                    THToast.show(getString(R.string.manage_heroes_unfollow_official_accounts_failed));
                                }
                                else
                                {
                                    Timber.e((Throwable) error, "onUserFollowFailed error");
                                    THToast.show(getString(R.string.manage_heroes_unfollow_failed));
                                }
                            }
                    ));
        }
        else
        {
            subscriptions.add(heroAlertDialogRxUtil.popAlertUnFollowHero(getActivity())
                    .filter(pair -> pair.second.equals(AlertDialogButtonConstants.POSITIVE_BUTTON_INDEX))
                    .subscribe(
                            pair -> {
                                THToast.show(getString(R.string.manage_heroes_unfollow_progress_message));
                                unfollow(clickedHeroDTO.getBaseKey());
                            },
                            error -> THToast.show(new THException(error))
                    ));
        }
    }

    protected void unfollow(@NonNull UserBaseKey userBaseKey)
    {
        subscriptions.add(
                AndroidObservable.bindFragment(
                        this,
                        new SimpleFollowUserAssistant(getActivity(), userBaseKey)
                                .launchUnFollowRx())
                        .subscribe(
                                profile -> fetchHeroes(),
                                error -> THToast.show(new THException(error))));
    }

    private void pushTimelineFragment(UserBaseKey userBaseKey)
    {
        Bundle args = new Bundle();
        thRouter.save(args, userBaseKey);
        navigator.get().pushFragment(PushableTimelineFragment.class, args);
    }

    private void handleGoMostSkilled()
    {
        // TODO this feels HACKy
        //navigator.popFragment();

        // TODO make it go to most skilled
        //navigator.goToTab(DashboardTabType.COMMUNITY);

        LeaderboardDefKey key =
                new LeaderboardDefKey(LeaderboardDefKeyKnowledge.MOST_SKILLED_ID);
        LeaderboardDefDTO dto = leaderboardDefCache.get().getValue(key);
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

    abstract protected void display(HeroDTOExtWrapper heroDTOExtWrapper);

    protected void display(List<HeroDTO> heroDTOs)
    {
        linkWith(heroDTOs, true);
    }

    public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        if (andDisplay)
        {
        }
    }

    public void linkWith(List<HeroDTO> heroDTOs, boolean andDisplay)
    {
        heroListAdapter.setItems(heroDTOs);
        if (andDisplay)
        {
            displayHeroList();
        }
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

    private HeroListMostSkilledClickedListener createHeroListMostSkilledClickedListener()
    {
        return new HeroListMostSkilledClickedListener();
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

    private class HeroListMostSkilledClickedListener implements View.OnClickListener
    {
        @Override public void onClick(View view)
        {
            handleGoMostSkilled();
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