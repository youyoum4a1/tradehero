package com.tradehero.th.fragments.social.hero;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroIdExtWrapper;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.leaderboard.BaseLeaderboardFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.social.FragmentUtils;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.social.HeroCache;
import com.tradehero.th.persistence.social.HeroType;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

abstract public class HeroesTabContentFragment extends BasePurchaseManagerFragment
        implements PullToRefreshBase.OnRefreshListener2<ListView>
{
    private static final String BUNDLE_KEY_FOLLOWER_ID =
            HeroesTabContentFragment.class.getName() + ".followerId";

    private HeroManagerViewContainer viewContainer;
    private ProgressDialog progressDialog;
    private HeroListItemAdapter heroListAdapter;
    // The follower whose heroes we are listing
    private UserBaseKey followerId;
    private UserProfileDTO userProfileDTO;
    private List<HeroDTO> heroDTOs;
    private HeroManagerInfoFetcher infoFetcher;

    @Inject public Lazy<HeroCache> heroCache;
    @Inject public HeroAlertDialogUtil heroAlertDialogUtil;
    /** when no heroes */
    @Inject Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject HeroTypeResourceDTOFactory heroTypeResourceDTOFactory;
    @Inject CurrentUserId currentUserId;

    public static void putFollowerId(Bundle args, UserBaseKey followerId)
    {
        args.putBundle(BUNDLE_KEY_FOLLOWER_ID, followerId.getArgs());
    }

    public static UserBaseKey getFollowerId(Bundle args)
    {
        return new UserBaseKey(args.getBundle(BUNDLE_KEY_FOLLOWER_ID));
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
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
        View view = inflater.inflate(R.layout.fragment_store_manage_heroes, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        this.viewContainer = new HeroManagerViewContainer(view);
        if (this.viewContainer.btnBuyMore != null)
        {
            this.viewContainer.btnBuyMore.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleBuyMoreClicked();
                }
            });
        }

        this.heroListAdapter = new HeroListItemAdapter(
                getActivity(),
                getActivity().getLayoutInflater(),
                /**R.layout.hero_list_item_empty_placeholder*/getEmptyViewLayout(),
                R.layout.hero_list_item,
                R.layout.hero_list_header,
                R.layout.hero_list_header);
        this.heroListAdapter.setHeroStatusButtonClickedListener(createHeroStatusButtonClickedListener());
        this.heroListAdapter.setFollowerId(followerId);
        this.heroListAdapter.setMostSkilledClicked(createHeroListMostSkiledClickedListener());
        if (this.viewContainer.pullToRefreshListView != null)
        {
            this.viewContainer.pullToRefreshListView.setOnRefreshListener(this);
        }
        if (this.viewContainer.heroListView != null)
        {
            this.viewContainer.heroListView.setAdapter(this.heroListAdapter);
            this.viewContainer.heroListView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id)
                        {
                            handleHeroClicked(parent, view, position, id);
                        }
                    }
            );
        }
        setListShown(false);
        this.infoFetcher = new HeroManagerInfoFetcher(
                new HeroManagerUserProfileCacheListener(),
                new HeroManagerHeroListCacheListener());
    }

    protected HeroListItemView.OnHeroStatusButtonClickedListener createHeroStatusButtonClickedListener()
    {
        return new HeroListItemView.OnHeroStatusButtonClickedListener()
        {
            @Override
            public void onHeroStatusButtonClicked(HeroListItemView heroListItemView,
                    HeroDTO heroDTO)
            {
                handleHeroStatusButtonClicked(heroDTO);
            }
        };
    }

    private void setListShown(boolean shown)
    {
        if (shown)
        {
            this.viewContainer.heroListView.setVisibility(View.VISIBLE);
            this.viewContainer.progressBar.setVisibility(View.INVISIBLE);
        }
        else
        {
            this.viewContainer.heroListView.setVisibility(View.INVISIBLE);
            this.viewContainer.progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE
                        | ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setTitle(getTitle());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();
        this.followerId = getFollowerId(getArguments());
        enablePullToRefresh(false);
        displayProgress(true);
        this.infoFetcher.fetch(this.followerId);
    }

    private boolean isCurrentUser()
    {
        UserBaseKey followerId = getFollowerId(getArguments());
        if (followerId != null && followerId.key != null && currentUserId != null)
        {
            return (followerId.key.intValue() == currentUserId.toUserBaseKey().key.intValue());
        }
        return false;
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
        if (this.followerId == null)
        {
            this.followerId = getFollowerId(getArguments());
        }

        // TODO rework this part to handle the reload in a manner similar to the
        // initial load, with passing the listener first.
        this.infoFetcher.reloadHeroes(this.followerId, new HeroManagerHeroListRefreshListener());
    }

    protected HeroTypeResourceDTO getHeroTypeResource()
    {
        return heroTypeResourceDTOFactory.create(getHeroType());
    }

    abstract protected HeroType getHeroType();

    @Override public void onPause()
    {
        if (this.progressDialog != null)
        {
            this.progressDialog.hide();
        }
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        if (this.infoFetcher != null)
        {
            this.infoFetcher.onDestroyView();
        }
        this.infoFetcher = null;

        if (this.heroListAdapter != null)
        {
            this.heroListAdapter.setHeroStatusButtonClickedListener(null);
            this.heroListAdapter.setMostSkilledClicked(null);
        }
        this.heroListAdapter = null;
        if (this.viewContainer.heroListView != null)
        {
            this.viewContainer.heroListView.setOnItemClickListener(null);
        }
        this.viewContainer = null;

        super.onDestroyView();
    }

    @Override protected PremiumFollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        return new PremiumFollowUserAssistant.OnUserFollowedListener()
        {
            @Override
            public void onUserFollowSuccess(UserBaseKey userFollowed,
                    UserProfileDTO currentUserProfileDTO)
            {
                Timber.d("onUserFollowSuccess");
                THToast.show(getString(R.string.manage_heroes_unfollow_success));
                linkWith(currentUserProfileDTO, true);
                if (infoFetcher != null)
                {
                    infoFetcher.fetchHeroes(followerId);
                }
            }

            @Override public void onUserFollowFailed(UserBaseKey userFollowed, Throwable error)
            {
                Timber.e(error, "onUserFollowFailed error");
                THToast.show(getString(R.string.manage_heroes_unfollow_failed));
            }
        };
    }

    private void handleBuyMoreClicked()
    {
        cancelOthersAndShowProductDetailList(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS);
    }

    private void handleHeroStatusButtonClicked(HeroDTO heroDTO)
    {
        handleHeroStatusChangeRequired(heroDTO);
    }

    private void handleHeroClicked(AdapterView<?> parent, View view, int position, long id)
    {
        pushTimelineFragment(((HeroDTO) parent.getItemAtPosition(position)).getBaseKey());
    }

    private void handleHeroStatusChangeRequired(final HeroDTO clickedHeroDTO)
    {
        if (!clickedHeroDTO.active)
        {
            //TODO hacked by alipay alex
            //heroAlertDialogUtil.popAlertFollowHero(getActivity(),
            //        new DialogInterface.OnClickListener()
            //        {
            //            @Override public void onClick(DialogInterface dialog, int which)
            //            {
                            premiumFollowUser(clickedHeroDTO.getBaseKey());
                        //}
                    //}
            //);
        }
        else
        {
            heroAlertDialogUtil.popAlertUnfollowHero(getActivity(),
                    new DialogInterface.OnClickListener()
                    {
                        @Override public void onClick(DialogInterface dialog, int which)
                        {
                            THToast.show(
                                    getString(R.string.manage_heroes_unfollow_progress_message));
                            unfollowUser(clickedHeroDTO.getBaseKey());
                        }
                    }
            );
        }
    }

    private void pushTimelineFragment(UserBaseKey userBaseKey)
    {
        Bundle args = new Bundle();
        args.putInt(PushableTimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userBaseKey.key);
        getDashboardNavigator().pushFragment(PushableTimelineFragment.class, args);
    }

    private void handleGoMostSkilled()
    {
        // TODO this feels HACKy
        //getDashboardNavigator().popFragment();

        // TODO make it go to most skilled
        //getDashboardNavigator().goToTab(DashboardTabType.COMMUNITY);

        LeaderboardDefKey key =
                new LeaderboardDefKey(LeaderboardDefKeyKnowledge.MOST_SKILLED_ID);
        LeaderboardDefDTO dto = leaderboardDefCache.get().get(key);
        Bundle bundle = new Bundle(getArguments());
        if (dto != null)
        {
            LeaderboardMarkUserListFragment.putLeaderboardDefKey(bundle, dto.getLeaderboardDefKey());
            bundle.putString(BaseLeaderboardFragment.BUNDLE_KEY_LEADERBOARD_DEF_TITLE, dto.name);
            bundle.putString(BaseLeaderboardFragment.BUNDLE_KEY_LEADERBOARD_DEF_DESC, dto.desc);
        }
        else
        {
            LeaderboardMarkUserListFragment.putLeaderboardDefKey(bundle, new LeaderboardDefKey(LeaderboardDefKeyKnowledge.MOST_SKILLED_ID));
            bundle.putString(BaseLeaderboardFragment.BUNDLE_KEY_LEADERBOARD_DEF_TITLE, getString(R.string.leaderboard_community_leaderboards));
        }
        getDashboardNavigator().pushFragment(LeaderboardMarkUserListFragment.class, bundle);
    }

    public void display(UserProfileDTO userProfileDTO)
    {
        linkWith(userProfileDTO, true);
    }

    abstract protected void display(HeroIdExtWrapper heroIds);

    protected void display(List<HeroDTO> heroDTOs)
    {
        linkWith(heroDTOs, true);
    }

    public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.userProfileDTO = userProfileDTO;
        if (andDisplay)
        {
            viewContainer.displayFollowCount(userProfileDTO);
            viewContainer.displayCoinStack(userProfileDTO);
        }
    }

    public void linkWith(List<HeroDTO> heroDTOs, boolean andDisplay)
    {
        this.heroDTOs = heroDTOs;
        heroListAdapter.setItems(this.heroDTOs);
        if (andDisplay)
        {
            displayHeroList();
        }
    }

    //<editor-fold desc="Display methods">
    public void display()
    {
        viewContainer.displayFollowCount(userProfileDTO);
        viewContainer.displayCoinStack(userProfileDTO);
        displayHeroList();
    }

    private void onRefreshCompleted()
    {
        if (viewContainer.pullToRefreshListView != null)
        {
            viewContainer.pullToRefreshListView.onRefreshComplete();
        }
    }

    private void enablePullToRefresh(boolean enable)
    {
        if (viewContainer.pullToRefreshListView != null)
        {
            if (!enable)
            {
                viewContainer.pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
            }
            else
            {
                viewContainer.pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
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
        if (viewContainer.progressBar != null)
        {
            viewContainer.progressBar.setVisibility(running ? View.VISIBLE : View.GONE);
        }
    }

    @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
    {
        Timber.d("onPullDownToRefresh");
        refreshContent();
    }

    @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
    {
        Timber.d("onPullUpToRefresh");
    }
    //</editor-fold>

    private HeroListMostSkilledClickedListener createHeroListMostSkiledClickedListener()
    {
        return new HeroListMostSkilledClickedListener();
    }

    private class HeroManagerUserProfileCacheListener
            implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override
        public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
        {
            if (key.equals(HeroesTabContentFragment.this.followerId))
            {
                display(value);
            }
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            Timber.e("Could not fetch user profile", error);
            THToast.show(R.string.error_fetch_user_profile);
        }
    }

    private class HeroManagerHeroListCacheListener
            implements DTOCacheNew.Listener<UserBaseKey, HeroIdExtWrapper>
    {
        @Override public void onDTOReceived(UserBaseKey key, HeroIdExtWrapper value)
        {
            //displayProgress(false);
            setListShown(true);
            display(value);
            enablePullToRefresh(true);
            notifyHeroesLoaded(value);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            displayProgress(false);
            setListShown(true);
            enablePullToRefresh(true);
            Timber.e(error, "Could not fetch heroes");
            THToast.show(R.string.error_fetch_hero);
        }
    }

    private class HeroManagerHeroListRefreshListener
            implements DTOCacheNew.Listener<UserBaseKey, HeroIdExtWrapper>
    {
        @Override public void onDTOReceived(UserBaseKey key, HeroIdExtWrapper value)
        {
            onRefreshCompleted();
            //setListShown(true);
            display(value);
            notifyHeroesLoaded(value);
            Timber.d("HeroManagerHeroListRefreshListener,onDTOReceived");
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            onRefreshCompleted();
            //setListShown(true);
            Timber.e(error, "HeroManagerHeroListRefreshListener,Could not fetch heroes");
            //THToast.show(R.string.error_fetch_hero);
        }
    }

    private class HeroListMostSkilledClickedListener implements View.OnClickListener
    {
        @Override public void onClick(View view)
        {
            handleGoMostSkilled();
        }
    }

    private void notifyHeroesLoaded(HeroIdExtWrapper value)
    {
        OnHeroesLoadedListener listener =
                FragmentUtils.getParent(this, OnHeroesLoadedListener.class);
        if (listener != null && !isDetached())
        {
            listener.onHerosLoaded(getHeroTypeResource(), value);
        }
    }
}