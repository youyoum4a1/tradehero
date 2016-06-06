package com.androidth.general.fragments.updatecenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import com.androidth.general.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.androidth.general.R;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.exception.THException;
import com.androidth.general.models.discussion.RunnableInvalidateMessageList;
import com.androidth.general.models.notification.RunnableInvalidateNotificationList;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.utils.GraphicUtil;
import com.androidth.general.utils.route.PreRoutable;
import com.androidth.general.utils.route.THRouter;
import com.androidth.general.widget.THTabView;
import javax.inject.Inject;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;

@PreRoutable(preOpenRunnables = {
        RunnableInvalidateMessageList.class,
        RunnableInvalidateNotificationList.class,
})
@Routable("updatecenter/:pageIndex")
public class UpdateCenterFragment extends BaseFragment
        implements OnTitleNumberChangeListener
{
    static final int FRAGMENT_LAYOUT_ID = 10000;
    public static final String REQUEST_UPDATE_UNREAD_COUNTER = ".updateUnreadCounter";

    @Inject UserProfileCacheRx userProfileCache;
    @Inject CurrentUserId currentUserId;

    @Inject THRouter thRouter;

    @RouteProperty("pageIndex") int selectedPageIndex = -1;
    private FragmentTabHost mTabHost;
    private BroadcastReceiver broadcastReceiver;

    public static void registerAliases(@NonNull THRouter router)
    {
        router.registerAlias("messages", "updatecenter/" + UpdateCenterTabType.Messages.ordinal());
        router.registerAlias("notifications", "updatecenter/" + UpdateCenterTabType.Notifications.ordinal());
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
        broadcastReceiver = createBroadcastReceiver();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), this.getChildFragmentManager(), FRAGMENT_LAYOUT_ID);
        TabWidget tabWidget = mTabHost.getTabWidget();
        if (tabWidget != null)
        // It otherwise fails in Robolectric because it does not have R.id.tabs in the TabHost
        {
            GraphicUtil.setBackgroundColorFromAttribute(tabWidget, R.attr.slidingTabBackground);
        }
        Bundle args = getArguments();
        if (args == null)
        {
            args = new Bundle();
        }
        UpdateCenterTabType[] types = UpdateCenterTabType.values();
        for (UpdateCenterTabType tabTitle : types)
        {
            args = new Bundle(args);
            THTabView tabView = THTabView.inflateWith(mTabHost.getTabWidget());
            String title = getString(tabTitle.titleRes, 0);
            tabView.setTitle(title);
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(title).setIndicator(tabView);
            mTabHost.addTab(tabSpec, tabTitle.tabClass, args);
        }

        return mTabHost;
    }

    @Override public void onResume()
    {
        super.onResume();
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
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(broadcastReceiver);
    }

    private void fetchUserProfile()
    {
        fetchUserProfile(false);
    }

    private void fetchUserProfile(boolean forceUpdate)
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userProfileCache.get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUserProfileCacheObserver()));
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        Fragment f = getCurrentFragment();
        if (f != null)
        {
            getCurrentFragment().onCreateOptionsMenu(menu, inflater);
        }

        setActionBarTitle(R.string.message_center_title);
        //inflater.inflate(R.menu.notification_center_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
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
        // TODO Questionable, as specified by Liang, it should not be needed to clear the tabs here
        //don't have to clear sub fragment to refresh data
        //clearTabs();

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        broadcastReceiver = null;
        super.onDestroy();
    }

    public Fragment getCurrentFragment()
    {
        if (mTabHost == null)
        {
            return null;
        }
        String tag = mTabHost.getCurrentTabTag();
        android.support.v4.app.FragmentManager fm = this.getChildFragmentManager();
        return fm.findFragmentByTag(tag);
    }

    private void changeTabTitleNumber(@NonNull UpdateCenterTabType tabType, int number)
    {
        THTabView tabView = (THTabView) mTabHost.getTabWidget().getChildAt(tabType.ordinal());
        tabView.setNumber(number);
    }

    @Override public void onTitleNumberChanged(@NonNull UpdateCenterTabType tabType, int number)
    {
        changeTabTitleNumber(tabType, number);
    }

    @NonNull protected Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileCacheObserver()
    {
        return new FetchUserProfileObserver();
    }

    private class FetchUserProfileObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(new THException(e));
        }
    }

    private void linkWith(@NonNull UserProfileDTO userProfileDTO, boolean andDisplay)
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
                fetchUserProfile(true);
            }
        };
    }
}
