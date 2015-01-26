package com.tradehero.th.fragments.leaderboard.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClickSticky;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.def.DrillDownLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.ExchangeContainerLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.ExchangeLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.leaderboard.FriendLeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardDefFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardDefListFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserPagerFragment;
import com.tradehero.th.fragments.social.PeopleSearchFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.models.leaderboard.LeaderboardDefDTOKnowledge;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCacheRx;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import timber.log.Timber;

@Routable("providers")
public class LeaderboardCommunityFragment extends BasePurchaseManagerFragment
        implements WithTutorial, View.OnClickListener
{
    @Inject Lazy<LeaderboardDefListCacheRx> leaderboardDefListCache;
    @Inject Analytics analytics;
    @Inject CommunityPageDTOFactory communityPageDTOFactory;
    @Inject Lazy<LeaderboardDefDTOKnowledge> leaderboardDefDTOKnowledge;
    @Inject LeaderboardCommunityTypeFactory leaderboardCommunityTypeFactory;

    @InjectView(R.id.community_screen) BetterViewAnimator communityScreen;
    @InjectView(R.id.leaderboard_community_list) StickyListHeadersListView leaderboardDefListView;

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClickSticky(R.id.leaderboard_community_list) void handleLeaderboardItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        LeaderboardDefDTO leaderboardDefDTO = (LeaderboardDefDTO) parent.getItemAtPosition(position);
        if (leaderboardDefDTO instanceof DrillDownLeaderboardDefDTO)
        {
            DrillDownLeaderboardDefDTO drillDownLeaderboardDefDTO = (DrillDownLeaderboardDefDTO) leaderboardDefDTO;
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboards_DrillDown));
            if (drillDownLeaderboardDefDTO instanceof ExchangeContainerLeaderboardDefDTO)
            {
                pushLeaderboardDefExchange(drillDownLeaderboardDefDTO);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled drillDownLeaderboardDefDTO " + drillDownLeaderboardDefDTO);
            }
        }
        else if (leaderboardDefDTOKnowledge.get().hasForexType(leaderboardDefDTO.id))
        {
            pushTabbedLeaderboardFragment(leaderboardDefDTO);
        }
        else
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboards_ShowLeaderboard));
            pushLeaderboardListViewFragment(leaderboardDefDTO);
        }
    }

    private BaseWebViewFragment webFragment;
    private LeaderboardCommunityAdapter leaderboardDefListAdapter;
    private int currentDisplayedChildLayoutId;
    @Nullable protected Subscription leaderboardDefListFetchSubscription;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        leaderboardDefListAdapter = new LeaderboardCommunityAdapter(
                getActivity(),
                R.layout.leaderboard_definition_item_view,
                leaderboardCommunityTypeFactory);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.leaderboard_community_screen, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        leaderboardDefListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
        leaderboardDefListView.setAdapter(leaderboardDefListAdapter);
    }

    @Override public void onStart()
    {
        super.onStart();
        // show either progress bar or def list, whichever last seen on this screen
        if (currentDisplayedChildLayoutId != 0)
        {
            communityScreen.setDisplayedChildByLayoutId(currentDisplayedChildLayoutId);
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.TabBar_Community));

        fetchLeaderboardDefList();
        // We came back into view so we have to forget the web fragment
        detachWebFragment();
    }

    @Override public void onStop()
    {
        unsubscribe(leaderboardDefListFetchSubscription);
        leaderboardDefListFetchSubscription = null;
        currentDisplayedChildLayoutId = communityScreen.getDisplayedChildLayoutId();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        leaderboardDefListAdapter = null;
        detachWebFragment();
        super.onDestroy();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.social_search_menu, menu);
        setActionBarTitle(R.string.dashboard_community);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO switch sorting type for leaderboard
        switch (item.getItemId())
        {
            case R.id.btn_search:
                pushSearchFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    private void detachWebFragment()
    {
        if (this.webFragment != null)
        {
            this.webFragment.setThIntentPassedListener(null);
        }
        this.webFragment = null;
    }

    private void fetchLeaderboardDefList()
    {
        unsubscribe(leaderboardDefListFetchSubscription);

        Observable<LeaderboardDefDTOList> leaderboardDefObservable =
                leaderboardDefListCache.get().get(new LeaderboardDefListKey())
                        .map(pair -> pair.second)
                        .map(leaderboardDefDTOs -> communityPageDTOFactory.collectFromCaches(null)) // TODO remove communityPageDTOFactory
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());

        leaderboardDefListFetchSubscription = AndroidObservable.bindFragment(
                this,
                leaderboardDefObservable)
                .doOnNext((i) -> communityScreen.setDisplayedChildByLayoutId(R.id.leaderboard_community_list))
                .subscribe(
                        leaderboardDefListAdapter::setItems,
                        (e) -> THToast.show(getString(R.string.error_fetch_leaderboard_def_list_key)));
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_leaderboard_community;
    }

    @Override public void onClick(View v)
    {
        if (v.getId() == R.id.error)
        {
            //if error view is click it means to reload the data
            communityScreen.setDisplayedChildByLayoutId(R.id.progress);
            fetchLeaderboardDefList();
        }
    }

    //<editor-fold desc="Navigation">
    protected void pushLeaderboardListViewFragment(@NonNull LeaderboardDefDTO dto)
    {
        Bundle bundle = new Bundle(getArguments());

        switch (dto.id)
        {
            case LeaderboardDefKeyKnowledge.FRIEND_ID:
                pushFriendsFragment(dto);
                break;
            case LeaderboardDefKeyKnowledge.HERO_ID:
                pushHeroFragment();
                break;
            case LeaderboardDefKeyKnowledge.FOLLOWER_ID:
                pushFollowerFragment();
                break;
            case LeaderboardDefKeyKnowledge.INVITE_FRIENDS_ID:
                pushInvitationFragment();
                break;
            default:
                Timber.d("LeaderboardMarkUserListFragment %s", bundle);
                LeaderboardMarkUserListFragment.putLeaderboardDefKey(bundle, dto.getLeaderboardDefKey());
                navigator.get().pushFragment(LeaderboardMarkUserListFragment.class, bundle);
                break;
        }
    }

    protected void pushFriendsFragment(LeaderboardDefDTO dto)
    {
        Bundle args = new Bundle();
        FriendLeaderboardMarkUserListFragment.putLeaderboardDefKey(args, dto.getLeaderboardDefKey());
        if (navigator != null)
        {
            navigator.get().pushFragment(FriendLeaderboardMarkUserListFragment.class, args);
        }
    }

    protected void pushHeroFragment()
    {
        Bundle bundle = new Bundle();
        HeroManagerFragment.putFollowerId(bundle, currentUserId.toUserBaseKey());
        OwnedPortfolioId applicablePortfolio = getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            HeroManagerFragment.putApplicablePortfolioId(bundle, applicablePortfolio);
        }
        navigator.get().pushFragment(HeroManagerFragment.class, bundle);
    }

    protected void pushFollowerFragment()
    {
        Bundle bundle = new Bundle();
        FollowerManagerFragment.putHeroId(bundle, currentUserId.toUserBaseKey());
        OwnedPortfolioId applicablePortfolio = getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            //FollowerManagerFragment.putApplicablePortfolioId(bundle, applicablePortfolio);
        }
        navigator.get().pushFragment(FollowerManagerFragment.class, bundle);
    }

    private void pushLeaderboardDefExchange(LeaderboardDefDTO leaderboardDefDTOExchange)
    {
        Bundle bundle = new Bundle(getArguments());
        (new ExchangeLeaderboardDefListKey()).putParameters(bundle);
        LeaderboardDefFragment.putLeaderboardDefKey(bundle, leaderboardDefDTOExchange.getLeaderboardDefKey());
        if (navigator != null)
        {
            navigator.get().pushFragment(LeaderboardDefListFragment.class, bundle);
        }
    }

    private void pushTabbedLeaderboardFragment(LeaderboardDefDTO leaderboardDefDTO)
    {
        Bundle bundle = new Bundle(getArguments());
        LeaderboardMarkUserPagerFragment.putLeaderboardDefKey(bundle, leaderboardDefDTO.getLeaderboardDefKey());
        if (navigator != null)
        {
            navigator.get().pushFragment(LeaderboardMarkUserPagerFragment.class, bundle);
        }
    }

    private void pushSearchFragment()
    {
        if (navigator != null)
        {
            navigator.get().pushFragment(PeopleSearchFragment.class, null);
        }
    }

    private void pushInvitationFragment()
    {
        if (navigator != null)
        {
            navigator.get().pushFragment(FriendsInvitationFragment.class);
        }
    }
    //</editor-fold>
}
