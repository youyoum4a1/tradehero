package com.tradehero.th.fragments.updatecenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TabHost;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.special.ResideMenu.ResideMenu;
import com.thoj.route.Routable;
import com.thoj.route.RouteProperty;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.social.AllRelationsFragment;
import com.tradehero.th.fragments.social.follower.SendMessageFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.THRouter;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

@Routable("updatecenter/:pageIndex")
public class UpdateCenterFragment extends BaseFragment
        implements PopupMenu.OnMenuItemClickListener,
        OnTitleNumberChangeListener,
        ResideMenu.OnMenuListener
{
    static final int FRAGMENT_LAYOUT_ID = 10000;
    public static final String REQUEST_UPDATE_UNREAD_COUNTER = ".updateUnreadCounter";

    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject THLocalyticsSession localyticsSession;
    @Inject Lazy<ResideMenu> resideMenuLazy;
    @Inject MessageHeaderListCache messageListCache;
    @Inject MessageHeaderCache messageHeaderCache;
    @Inject THRouter thRouter;

    @RouteProperty("pageIndex") int selectedPageIndex = -1;

    private FragmentTabHost mTabHost;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    private BroadcastReceiver broadcastReceiver;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        thRouter.inject(this);
        userProfileCacheListener = createUserProfileCacheListener();
        broadcastReceiver = createBroadcastReceiver();
        Timber.d("onCreate");
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        Timber.d("onCreateView");
        return addTabs();
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override public void onResume()
    {
        super.onResume();

        Timber.d("onResume fetchUserProfile");
        fetchUserProfile();

        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(broadcastReceiver,
                        new IntentFilter(REQUEST_UPDATE_UNREAD_COUNTER));

        if (selectedPageIndex > 0)
        {
            mTabHost.setCurrentTab(selectedPageIndex);
        }
    }

    @Override public void onPause()
    {
        super.onPause();

        Timber.d("onPause");
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(broadcastReceiver);
    }

    private void fetchUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    private void detachUserProfileCache()
    {
        userProfileCache.unregister(userProfileCacheListener);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        Fragment f = getCurrentFragment();
        if (f != null)
        {
            ((SherlockFragment) getCurrentFragment()).onCreateOptionsMenu(menu, inflater);
        }

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_USE_LOGO);
        actionBar.setTitle(R.string.message_center_title);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setLogo(R.drawable.icn_actionbar_hamburger);
        inflater.inflate(R.menu.notification_center_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                resideMenuLazy.get().openMenu();
                return true;
            case R.id.menu_private:
                localyticsSession.tagEvent(LocalyticsConstants.Notification_New_Message);
                ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator()
                        .pushFragment(AllRelationsFragment.class);
                return true;
            case R.id.menu_broadcast:
                jumpToSendBroadcastMessage();
                return true;
        }
        Fragment f = getCurrentFragment();
        if (f != null)
        {
            boolean handled = ((SherlockFragment) getCurrentFragment()).onOptionsItemSelected(item);
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
            ((SherlockFragment) getCurrentFragment()).onPrepareOptionsMenu(menu);
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

    @Override public void onDestroyOptionsMenu()
    {
        Fragment f = getCurrentFragment();
        if (f != null)
        {
            f.onDestroyOptionsMenu();
        }
        Timber.d("onDestroyOptionsMenu");

        super.onDestroyOptionsMenu();
    }

    @Override
    public boolean onMenuItemClick(android.view.MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_private:
                localyticsSession.tagEvent(LocalyticsConstants.Notification_New_Message);
                ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator()
                        .pushFragment(AllRelationsFragment.class);
                return true;
            case R.id.menu_broadcast:
                jumpToSendBroadcastMessage();
                return true;
            default:
                return false;
        }
    }

    private void jumpToSendBroadcastMessage()
    {
        localyticsSession.tagEvent(LocalyticsConstants.Notification_New_Broadcast);
        Bundle args = new Bundle();
        args.putInt(SendMessageFragment.KEY_DISCUSSION_TYPE,
                DiscussionType.BROADCAST_MESSAGE.value);
        args.putInt(SendMessageFragment.KEY_MESSAGE_TYPE,
                MessageType.BROADCAST_ALL_FOLLOWERS.typeId);
        ((DashboardActivity) getActivity()).getDashboardNavigator().pushFragment(SendMessageFragment.class, args);
    }

    @Override public void onStop()
    {
        super.onStop();
        Timber.d("onStop");
    }

    @Override public void onDestroyView()
    {
        // TODO Questionable, as specified by Liang, it should not be needed to clear the tabs here
        Timber.d("onDestroyView");
        //don't have to clear sub fragment to refresh data
        //clearTabs();
        detachUserProfileCache();

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        Timber.d("onDestroy");
        userProfileCacheListener = null;
        broadcastReceiver = null;
        super.onDestroy();
    }

    private View addTabs()
    {
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), ((Fragment) this).getChildFragmentManager(), FRAGMENT_LAYOUT_ID);
        //mTabHost.setOnTabChangedListener(new HeroManagerOnTabChangeListener());
        Bundle args = getArguments();
        if (args == null)
        {
            args = new Bundle();
        }
        UpdateCenterTabType[] types = UpdateCenterTabType.values();
        for (UpdateCenterTabType tabTitle : types)
        {
            args = new Bundle(args);
            TitleTabView tabView = (TitleTabView) LayoutInflater.from(getActivity())
                    .inflate(R.layout.message_tab_item, mTabHost.getTabWidget(), false);
            String title = getString(tabTitle.titleRes, 0);
            tabView.setTitle(title);
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(title).setIndicator(tabView);
            mTabHost.addTab(tabSpec, tabTitle.tabClass, args);
        }

        return mTabHost;
    }

    private void clearTabs()
    {
        if (mTabHost != null)
        {
            android.support.v4.app.FragmentManager fm = ((Fragment) this).getChildFragmentManager();
            List<Fragment> fragmentList = fm.getFragments();
            Timber.d("fragmentList %s", fragmentList);
            if (fragmentList != null && fragmentList.size() > 0)
            {
                FragmentTransaction ft = fm.beginTransaction();
                for (Fragment f : fragmentList)
                {
                    if (f != null)
                    {
                        ft.remove(f);
                    }
                }
                //java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
                //TODO this will crash when onDestroy alex
                ft.commitAllowingStateLoss();
                fm.executePendingTransactions();
            }

            mTabHost.clearAllTabs();
            int tabCount = mTabHost.getTabWidget().getTabCount();
            mTabHost = null;
        }
    }

    public Fragment getCurrentFragment()
    {
        if (mTabHost == null)
        {
            return null;
        }
        String tag = mTabHost.getCurrentTabTag();
        android.support.v4.app.FragmentManager fm = ((Fragment) this).getChildFragmentManager();
        return fm.findFragmentByTag(tag);
    }

    private void changeTabTitleNumber(@NotNull UpdateCenterTabType tabType, int number)
    {
        @NotNull TitleTabView tabView = (TitleTabView) mTabHost.getTabWidget().getChildAt(tabType.ordinal());
        tabView.setTitleNumber(number);
    }

    @Override public void onTitleNumberChanged(@NotNull UpdateCenterTabType tabType, int number)
    {
        changeTabTitleNumber(tabType, number);
    }

    @NotNull protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new FetchUserProfileListener();
    }

    private class FetchUserProfileListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override
        public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, Throwable error)
        {
            THToast.show(new THException(error));
        }
    }

    private void linkWith(@NotNull UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        if (andDisplay)
        {
            changeTabTitleNumber(
                    UpdateCenterTabType.Messages,
                    userProfileDTO.unreadMessageThreadsCount);
            changeTabTitleNumber(
                    UpdateCenterTabType.Notifications,
                    userProfileDTO.unreadNotificationsCount);
        }
    }

    private BroadcastReceiver createBroadcastReceiver()
    {
        return new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
                fetchUserProfile();
            }
        };
    }

    @Override public void openMenu()
    {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment instanceof ResideMenu.OnMenuListener)
        {
            ((ResideMenu.OnMenuListener) currentFragment).openMenu();
        }
    }

    @Override public void closeMenu()
    {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment instanceof ResideMenu.OnMenuListener)
        {
            ((ResideMenu.OnMenuListener) currentFragment).closeMenu();
        }
    }
}
