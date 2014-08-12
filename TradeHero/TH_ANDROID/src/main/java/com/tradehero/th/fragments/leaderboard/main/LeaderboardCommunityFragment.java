package com.tradehero.th.fragments.leaderboard.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.special.ResideMenu.ResideMenu;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.leaderboard.SectorContainerLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.DrillDownLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.ExchangeContainerLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.ExchangeLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.SectorLeaderboardDefListKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.leaderboard.FriendLeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardDefListFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.social.PeopleSearchFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderIntent;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import timber.log.Timber;

@Routable("providers")
public class LeaderboardCommunityFragment extends BasePurchaseManagerFragment
        implements WithTutorial, View.OnClickListener
{
    @Inject Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject CurrentUserId currentUserId;
    @Inject ProviderUtil providerUtil;
    @Inject Analytics analytics;
    @Inject Lazy<ResideMenu> resideMenuLazy;
    @Inject CommunityPageDTOFactory communityPageDTOFactory;
    @Inject UserProfileCache userProfileCache;

    @InjectView(R.id.community_screen) BetterViewAnimator communityScreen;
    @InjectView(android.R.id.list) StickyListHeadersListView leaderboardDefListView;

    private THIntentPassedListener thIntentPassedListener;
    private BaseWebViewFragment webFragment;
    private LeaderboardCommunityAdapter leaderboardDefListAdapter;
    private int currentDisplayedChildLayoutId;
    protected DTOCacheNew.Listener<LeaderboardDefListKey, LeaderboardDefDTOList> leaderboardDefListFetchListener;
    @Nullable protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected UserProfileDTO currentUserProfileDTO;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.thIntentPassedListener = new LeaderboardCommunityTHIntentPassedListener();
        leaderboardDefListFetchListener = createDefKeyListListener();
        this.userProfileCacheListener = createUserProfileListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_community_screen, container, false);
        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
    }

    @Override public void onStart()
    {
        super.onStart();
        leaderboardDefListView.setOnItemClickListener(createItemClickListener());
        // show either progress bar or def list, whichever last seen on this screen
        if (currentDisplayedChildLayoutId != 0)
        {
            communityScreen.setDisplayedChildByLayoutId(currentDisplayedChildLayoutId);
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchCurrentUserProfile();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.TabBar_Community));

        // We came back into view so we have to forget the web fragment
        detachWebFragment();
    }

    @Override public void onStop()
    {
        detachLeaderboardDefListCacheFetchTask();
        detachUserProfileCache();
        currentDisplayedChildLayoutId = communityScreen.getDisplayedChildLayoutId();
        leaderboardDefListView.setOnItemClickListener(null);
        super.onStop();
    }

    @Override public void onDestroy()
    {
        leaderboardDefListFetchListener = null;
        this.thIntentPassedListener = null;
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

            case R.id.btn_add:
                pushInvitationFragment();
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

    protected void detachUserProfileCache()
    {
        userProfileCache.unregister(userProfileCacheListener);
    }

    protected void fetchCurrentUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    //<editor-fold desc="Data Fetching">
    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileListener()
    {
        return new LeaderboardCommunityUserProfileCacheListener();
    }

    protected class LeaderboardCommunityUserProfileCacheListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            setCurrentUserProfileDTO(value);
            loadLeaderboardData();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            Timber.e("Failed to download current UserProfile", error);
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }

    protected void setCurrentUserProfileDTO(@NotNull UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
    }

    private void detachLeaderboardDefListCacheFetchTask()
    {
        leaderboardDefListCache.get().unregister(leaderboardDefListFetchListener);
    }

    private void loadLeaderboardData()
    {
        fetchLeaderboardDefList();
    }

    private void fetchLeaderboardDefList()
    {
        detachLeaderboardDefListCacheFetchTask();
        leaderboardDefListCache.get().register(new LeaderboardDefListKey(), leaderboardDefListFetchListener);
        leaderboardDefListCache.get().getOrFetchAsync(new LeaderboardDefListKey());
    }

    protected DTOCacheNew.Listener<LeaderboardDefListKey, LeaderboardDefDTOList> createDefKeyListListener()
    {
        return new LeaderboardCommunityLeaderboardDefKeyListListener();
    }

    protected class LeaderboardCommunityLeaderboardDefKeyListListener implements DTOCacheNew.Listener<LeaderboardDefListKey, LeaderboardDefDTOList>
    {
        @Override public void onDTOReceived(@NotNull LeaderboardDefListKey key, @NotNull LeaderboardDefDTOList value)
        {
            recreateAdapter();
        }

        @Override public void onErrorThrown(@NotNull LeaderboardDefListKey key, @NotNull Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_leaderboard_def_list_key));
            Timber.e(error, "Error fetching the leaderboard def key list %s", key);
        }
    }
    //</editor-fold>

    protected AdapterView.OnItemClickListener createItemClickListener()
    {
        return new LeaderboardCommunityOnItemClickListener();
    }

    protected class LeaderboardCommunityOnItemClickListener implements AdapterView.OnItemClickListener
    {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
        {
            Object item = adapterView.getItemAtPosition(position);
            if (item instanceof LeaderboardDefCommunityPageDTO)
            {
                handleLeaderboardItemClicked(((LeaderboardDefCommunityPageDTO) item).leaderboardDefDTO);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled item type " + item);
            }
        }
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_leaderboard_community;
    }

    protected class LeaderboardCommunityTHIntentPassedListener implements THIntentPassedListener
    {
        @Override public void onIntentPassed(THIntent thIntent)
        {
            Timber.d("LeaderboardCommunityTHIntentPassedListener " + thIntent);
            if (thIntent instanceof ProviderIntent)
            {
                // Just in case the user has enrolled
                portfolioCompactListCache.invalidate(currentUserId.toUserBaseKey());
            }

            if (thIntent instanceof ProviderPageIntent)
            {
                Timber.d("Intent is ProviderPageIntent");
                if (webFragment != null)
                {
                    Timber.d("Passing on %s", ((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                    webFragment.loadUrl(((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                }
                else
                {
                    Timber.d("WebFragment is null");
                }
            }
            else
            {
                Timber.w("Unhandled intent %s", thIntent);
            }
        }
    }

    protected void recreateAdapter()
    {
        communityScreen.setDisplayedChildByLayoutId(android.R.id.list);
        if(leaderboardDefListAdapter != null)
        {
            leaderboardDefListAdapter.clear();
        }
        leaderboardDefListAdapter = createAdapter();
        leaderboardDefListAdapter.addAll(communityPageDTOFactory.collectFromCaches(currentUserProfileDTO.countryCode));
        leaderboardDefListView.setAdapter(leaderboardDefListAdapter);
    }

    protected LeaderboardCommunityAdapter createAdapter()
    {
        return new LeaderboardCommunityAdapter(
                getActivity(),
                R.layout.leaderboard_definition_item_view_community);
    }

    /**
     * TODO to show user detail of the error
     */
    private void handleFailToReceiveLeaderboardDefKeyList()
    {
        communityScreen.setDisplayedChildByLayoutId(R.id.error);
        View displayedChild = communityScreen.getChildAt(communityScreen.getDisplayedChild());
        displayedChild.setOnClickListener(this);
    }

    @Override public void onClick(View v)
    {
        if (v.getId() == R.id.error)
        {
            //if error view is click it means to reload the data
            communityScreen.setDisplayedChildByLayoutId(R.id.progress);
            loadLeaderboardData();
        }
    }

    //<editor-fold desc="Navigation">
    private void handleLeaderboardItemClicked(@NotNull LeaderboardDefDTO leaderboardDefDTO)
    {
        if (leaderboardDefDTO instanceof DrillDownLeaderboardDefDTO)
        {
            DrillDownLeaderboardDefDTO drillDownLeaderboardDefDTO = (DrillDownLeaderboardDefDTO) leaderboardDefDTO;
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboards_DrillDown));
            if (drillDownLeaderboardDefDTO instanceof SectorContainerLeaderboardDefDTO)
            {
                pushLeaderboardDefSector();
            }
            else if (drillDownLeaderboardDefDTO instanceof ExchangeContainerLeaderboardDefDTO)
            {
                pushLeaderboardDefExchange();
            }
            else
            {
                throw new IllegalArgumentException("Unhandled drillDownLeaderboardDefDTO " + drillDownLeaderboardDefDTO);
            }
        }
        else
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboards_ShowLeaderboard));
            pushLeaderboardListViewFragment(leaderboardDefDTO);
        }
    }

    protected void pushLeaderboardListViewFragment(@NotNull LeaderboardDefDTO dto)
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
            default:
                Timber.d("LeaderboardMarkUserListFragment %s", bundle);
                LeaderboardMarkUserListFragment.putLeaderboardDefKey(bundle, dto.getLeaderboardDefKey());
                getDashboardNavigator().pushFragment(LeaderboardMarkUserListFragment.class, bundle);
                break;
        }
    }

    protected void pushFriendsFragment(LeaderboardDefDTO dto)
    {
        Bundle args = new Bundle();

        FriendLeaderboardMarkUserListFragment.putLeaderboardDefKey(args, dto.getLeaderboardDefKey());

        getDashboardNavigator().pushFragment(FriendLeaderboardMarkUserListFragment.class, args);
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
        getDashboardNavigator().pushFragment(HeroManagerFragment.class, bundle);
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
        getDashboardNavigator().pushFragment(FollowerManagerFragment.class, bundle);
    }

    private void pushLeaderboardDefSector()
    {
        Bundle bundle = new Bundle(getArguments());
        (new SectorLeaderboardDefListKey()).putParameters(bundle);
        DashboardNavigator navigator = getDashboardNavigator();
        if (navigator != null)
        {
            navigator.pushFragment(LeaderboardDefListFragment.class, bundle);
        }
    }

    private void pushLeaderboardDefExchange()
    {
        Bundle bundle = new Bundle(getArguments());
        (new ExchangeLeaderboardDefListKey()).putParameters(bundle);
        DashboardNavigator navigator = getDashboardNavigator();
        if (navigator != null)
        {
            navigator.pushFragment(LeaderboardDefListFragment.class, bundle);
        }
    }

    private void pushSearchFragment()
    {
        DashboardNavigator navigator = getDashboardNavigator();
        if (navigator != null)
        {
            navigator.pushFragment(PeopleSearchFragment.class, null);
        }
    }

    private void pushInvitationFragment()
    {
        DashboardNavigator navigator = getDashboardNavigator();
        if (navigator != null)
        {
            navigator.pushFragment(FriendsInvitationFragment.class);
        }
    }
    //</editor-fold>
}
