package com.tradehero.th.fragments.social.follower;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.OnMovableBottomTranslateListener;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.social.follower.AllHeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.FreeHeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.models.social.follower.PremiumHeroTypeResourceDTO;
import com.tradehero.th.persistence.social.HeroType;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.GraphicUtil;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.THTabView;
import dagger.Lazy;
import java.text.MessageFormat;
import java.util.ArrayList;
import javax.inject.Inject;
import timber.log.Timber;

@Routable({
        "user/me/followers",
        "user/me/followers/tab-index/:tabIndexMe",
        "user/id/:heroId/followers",
        "user/id/:heroIdWithTab/followers/tab-index/:tabIndex",
        "user/id/:heroIdForAll/followers/all",
        "user/id/:heroIdForFree/followers/free",
        "user/id/:heroIdForPremium/followers/premium",
})
public class FollowerManagerFragment extends DashboardFragment
        implements OnFollowersLoadedListener
{
    static final int FRAGMENT_LAYOUT_ID = 10000;
    private static final String BUNDLE_KEY_HERO_ID = FollowerManagerFragment.class.getName() + ".heroId";

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject THRouter router;

    @RouteProperty("tabIndexMe") Integer routedTabIndexMe;
    @RouteProperty("heroId") Integer routedHeroId;
    @RouteProperty("heroIdWithTab") Integer routedHeroIdWithTab;
    @RouteProperty("heroIdForAll") Integer routedHeroIdForAll;
    @RouteProperty("heroIdForFree") Integer routedHeroIdForFree;
    @RouteProperty("heroIdForPremium") Integer routedHeroIdForPremium;
    @RouteProperty("tabIndex") Integer routedTabIndex;

    private UserBaseKey heroId;
    @InjectView(android.R.id.tabhost) FragmentTabHost mTabHost;
    @InjectView(R.id.send_message_broadcast) View broadcastView;

    private FollowerSummaryDTO followerSummaryDTO;

    public static void putHeroId(@NonNull Bundle args, @NonNull UserBaseKey heroId)
    {
        args.putBundle(BUNDLE_KEY_HERO_ID, heroId.getArgs());
    }

    @Nullable public static UserBaseKey getHeroId(@NonNull Bundle args)
    {
        Bundle heroBundle = args.getBundle(BUNDLE_KEY_HERO_ID);
        if (heroBundle != null)
        {
            return new UserBaseKey(heroBundle);
        }
        return null;
    }

    public static void registerAliases(@NonNull THRouter router)
    {
        router.registerAlias("user/me/followers/all", "user/me/followers/tab-index/" + new AllHeroTypeResourceDTO().followerTabIndex);
        router.registerAlias("user/me/followers/free", "user/me/followers/tab-index/" + new FreeHeroTypeResourceDTO().followerTabIndex);
        router.registerAlias("user/me/followers/premium", "user/me/followers/tab-index/" + new PremiumHeroTypeResourceDTO().followerTabIndex);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        router.inject(this);
        UserBaseKey heroId = getHeroId(getArguments());
        if (heroId == null)
        {
            if (routedHeroId != null)
            {
                heroId = new UserBaseKey(routedHeroId);
            }
            else if (routedHeroIdWithTab != null)
            {
                heroId = new UserBaseKey(routedHeroIdWithTab);
            }
            else if (routedHeroIdForAll != null)
            {
                heroId = new UserBaseKey(routedHeroIdForAll);
            }
            else if (routedHeroIdForFree != null)
            {
                heroId = new UserBaseKey(routedHeroIdForFree);
            }
            else if (routedHeroIdForPremium != null)
            {
                heroId = new UserBaseKey(routedHeroIdForPremium);
            }
            else
            {
                heroId = currentUserId.toUserBaseKey();
            }
        }
        this.heroId = heroId;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        setActionBarTitle(R.string.social_followers);

        Fragment f = getCurrentFragment();
        if (f != null)
        {
            getCurrentFragment().onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        Fragment f = getCurrentFragment();
        if (f != null)
        {
            boolean handled = getCurrentFragment().onOptionsItemSelected(item);
            if (handled)
            {
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        Fragment f = getCurrentFragment();
        if (f != null)
        {
            getCurrentFragment().onPrepareOptionsMenu(menu);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override public void onOptionsMenuClosed(android.view.Menu menu)
    {
        Fragment f = getCurrentFragment();
        if (f != null)
        {
            getCurrentFragment().onOptionsMenuClosed(menu);
        }

        super.onOptionsMenuClosed(menu);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store_manage_followers_2, container, false);
        ButterKnife.inject(this, view);
        addTabs();
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        showSendMessageLayoutIfNecessary();
    }

    @Override public void onResume()
    {
        super.onResume();
        fragmentElements.get().getMovableBottom().setOnMovableBottomTranslateListener(new OnMovableBottomTranslateListener()
        {
            @Override public void onTranslate(float x, float y)
            {
                broadcastView.setTranslationY(y);
            }
        });
    }

    @Override public void onPause()
    {
        fragmentElements.get().getMovableBottom().setOnMovableBottomTranslateListener(null);
        super.onPause();
    }

    @Override public void onDestroyOptionsMenu()
    {
        Fragment f = getCurrentFragment();
        if (f != null)
        {
            f.onDestroyOptionsMenu();
        }

        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        mTabHost.setOnTabChangedListener(null);
        mTabHost = null;
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    private void showSendMessageLayoutIfNecessary()
    {
        if (heroId.equals(currentUserId.toUserBaseKey()))
        {
            broadcastView.setVisibility(View.VISIBLE);
        }
        else
        {
            broadcastView.setVisibility(View.GONE);
        }
    }

    private Fragment getCurrentFragment()
    {
        if (mTabHost == null)
        {
            return null;
        }
        String tag = mTabHost.getCurrentTabTag();
        android.support.v4.app.FragmentManager fm = this.getChildFragmentManager();
        return fm.findFragmentByTag(tag);
    }

    private View addTabs()
    {
        // Nested fragments need ChildFragmentManager
        //http://developer.android.com/about/versions/android-4.2.html#NestedFragments
        mTabHost.setup(getActivity(), this.getChildFragmentManager(), FRAGMENT_LAYOUT_ID);
        for (HeroTypeResourceDTO resourceDTO : getTabResourceDTOs())
        {
            addTab(resourceDTO);
        }
        GraphicUtil.setBackground(mTabHost.getTabWidget(), getResources().getDrawable(R.drawable.bar_background));
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {
            @Override public void onTabChanged(String tabId)
            {
                FollowerManagerFragment.this.onTabChanged(tabId);
            }
        });
        if (routedTabIndex != null)
        {
            mTabHost.setCurrentTab(routedTabIndex);
            routedTabIndex = null;
        }
        else if (routedTabIndexMe != null)
        {
            mTabHost.setCurrentTab(routedTabIndexMe);
            routedTabIndexMe = null;
        }
        else if (routedHeroIdForAll != null)
        {
            mTabHost.setCurrentTab(new AllHeroTypeResourceDTO().followerTabIndex);
        }
        else if (routedHeroIdForFree != null)
        {
            mTabHost.setCurrentTab(new FreeHeroTypeResourceDTO().followerTabIndex);
        }
        else if (routedHeroIdForPremium != null)
        {
            mTabHost.setCurrentTab(new PremiumHeroTypeResourceDTO().followerTabIndex);
        }
        return mTabHost;
    }

    @NonNull protected ArrayList<HeroTypeResourceDTO> getTabResourceDTOs()
    {
        return HeroTypeResourceDTOFactory.getListOfHeroType();
    }

    private void addTab(@NonNull HeroTypeResourceDTO resourceDTO)
    {
        Bundle args = new Bundle();
        FollowerManagerTabFragment.putHeroId(args, heroId);

        String title = MessageFormat.format(getString(resourceDTO.followerTabTitleRes), 0);
        THTabView tabIndicator =
                THTabView.inflateWith(mTabHost.getTabWidget());
        tabIndicator.setTitle(title);
        TabHost.TabSpec tabSpec = mTabHost.newTabSpec(title).setIndicator(tabIndicator);
        mTabHost.addTab(tabSpec, resourceDTO.followerContentFragmentClass, args);
    }

    public void onTabChanged(String tabId)
    {
        displayBroadcastView();
    }

    public void displayBroadcastView()
    {
        if (followerSummaryDTO == null)
        {
            broadcastView.setVisibility(View.GONE);
        }
        else
        {
            broadcastView.setVisibility(View.VISIBLE);
            HeroTypeResourceDTO heroTypeResourceDTO = getTabResourceDTOs().get(mTabHost.getCurrentTab());
            int followerCount;
            if (heroTypeResourceDTO instanceof FreeHeroTypeResourceDTO)
            {
                followerCount = followerSummaryDTO.getFreeFollowerCount();
            }
            else if (heroTypeResourceDTO instanceof PremiumHeroTypeResourceDTO)
            {
                followerCount = followerSummaryDTO.getPaidFollowerCount();
            }
            else if (heroTypeResourceDTO instanceof AllHeroTypeResourceDTO)
            {
                followerCount = followerSummaryDTO.getFreeFollowerCount() + followerSummaryDTO.getPaidFollowerCount();
            }
            else
            {
                throw new IllegalArgumentException("Unhandled " + heroTypeResourceDTO.getClass());
            }
            broadcastView.setEnabled(followerCount > 0);
        }
    }

    private void changeTabTitle(@NonNull HeroTypeResourceDTO resourceDTO, int count)
    {
        THTabView titleView = (THTabView) mTabHost.getTabWidget()
                .getChildTabViewAt(resourceDTO.followerTabIndex);
        String title = MessageFormat.format(getString(resourceDTO.followerTabTitleRes), count);
        titleView.setTitle(title);
    }

    @Override public void onFollowerLoaded(int page, FollowerSummaryDTO value)
    {
        this.followerSummaryDTO = value;
        displayBroadcastView();
        if (!isDetached())
        {
            //remove the function to send message
            //setMessageLayoutShown(false);
            int paid = value.getPaidFollowerCount();
            int free = value.getFreeFollowerCount();

            changeTabTitle(new PremiumHeroTypeResourceDTO(), paid);
            changeTabTitle(new FreeHeroTypeResourceDTO(), free);
            changeTabTitle(new AllHeroTypeResourceDTO(), paid + free);

            updateUserProfileCache(value);
        }
    }

    private void updateUserProfileCache(FollowerSummaryDTO value)
    {
        // TODO synchronization problem
        UserBaseKey userBaseKey = currentUserId.toUserBaseKey();
        UserProfileDTO userProfileDTO = userProfileCache.get().getCachedValue(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            userProfileDTO.paidFollowerCount = value.getPaidFollowerCount();
            userProfileDTO.freeFollowerCount = value.getFreeFollowerCount();
            userProfileDTO.allFollowerCount = userProfileDTO.paidFollowerCount + userProfileDTO.freeFollowerCount;
            userProfileCache.get().onNext(userBaseKey, userProfileDTO);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.send_message_broadcast)
    protected void sendMessageBroadcastClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        goToMessagePage(DiscussionType.BROADCAST_MESSAGE);
    }

    private void goToMessagePage(DiscussionType discussionType)
    {
        int page = mTabHost.getCurrentTab();
        HeroType followerType = HeroType.fromId(page);

        Bundle args = new Bundle();
        MessageType messageType;
        switch (followerType)
        {
            case ALL:
                messageType = MessageType.BROADCAST_ALL_FOLLOWERS;
                break;
            case PREMIUM:
                messageType = MessageType.BROADCAST_PAID_FOLLOWERS;
                break;
            case FREE:
                messageType = MessageType.BROADCAST_FREE_FOLLOWERS;
                break;
            default:
                throw new IllegalStateException("unknown followerType!");
        }

        SendMessageFragment.putMessageType(args, messageType);
        Timber.d("goToMessagePage index:%d, tabIndex:%d, followerType:%s, discussionType:%s", page,
                page, followerType, discussionType);
        navigator.get().pushFragment(SendMessageFragment.class, args);
    }
}
