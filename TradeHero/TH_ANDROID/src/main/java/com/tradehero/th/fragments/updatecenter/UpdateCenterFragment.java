package com.tradehero.th.fragments.updatecenter;

import android.os.Bundle;
import android.support.v4.r11.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.localytics.android.LocalyticsSession;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.base.BaseFragment;
import java.util.ArrayList;
import java.util.List;
import com.tradehero.th.fragments.social.AllRelationsFragment;
import com.tradehero.th.fragments.social.follower.SendMessageFragment;
import com.tradehero.th.utils.LocalyticsConstants;
import java.util.Arrays;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by thonguyen on 3/4/14.
 */
public class UpdateCenterFragment extends BaseFragment /*DashboardFragment*/ implements PopupMenu.OnMenuItemClickListener, OnTitleNumberChangeListener
{
    public static final String KEY_PAGE = "page";

    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;

    private FragmentTabHost mTabHost;
    private DTOCache.Listener<UserBaseKey, UserProfileDTO> fetchUserProfileListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> fetchUserProfileTask;
    private MenuItem mMenuFollow;
    private ImageButton mNewMsgButton;

    @Inject LocalyticsSession localyticsSession;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        fetchUserProfileListener = new FetchUserProfileListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return addTabs();
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override public void onResume()
    {
        super.onResume();

        fetchUserProfile();
    }

    private void fetchUserProfile()
    {
        detachUserProfileTask();

        fetchUserProfileTask = userProfileCache.getOrFetch(currentUserId.toUserBaseKey(), false, fetchUserProfileListener);
        fetchUserProfileTask.execute();
    }

    private void detachUserProfileTask()
    {
        if (fetchUserProfileTask != null)
        {
            fetchUserProfileTask.setListener(null);
        }
        fetchUserProfileTask = null;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.update_center_title);
        inflater.inflate(R.menu.notification_center_menu, menu);

        mMenuFollow = menu.findItem(R.id.btn_new_message);
        mNewMsgButton =
                (ImageButton) mMenuFollow.getActionView().findViewById(R.id.new_message_button);
        if (mNewMsgButton != null)
        {
            mNewMsgButton.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    showPopup(v);
                }
            });
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.inflate(R.menu.notification_new_message_menu);
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(android.view.MenuItem item) {
        switch (item.getItemId()) {
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
        args.putInt(SendMessageFragment.KEY_DISCUSSION_TYPE, DiscussionType.BROADCAST_MESSAGE.value);
        args.putInt(SendMessageFragment.KEY_MESSAGE_TYPE, MessageType.BROADCAST_ALL_FOLLOWERS.typeId);
        ((DashboardActivity) getActivity()).getDashboardNavigator().pushFragment(
                SendMessageFragment.class, args);
    }

    @Override public void onDestroyView()
    {
        // TODO Questionable, as specified by Liang, it should not be needed to clear the tabs here
        //clearTabs();

        detachUserProfileTask();

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        fetchUserProfileListener = null;

        super.onDestroy();
    }

    private View addTabs()
    {
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), 11111);

        Bundle args = getArguments();
        if (args == null)
        {
            args = new Bundle();
        }

        UpdateCenterTabType[] types = UpdateCenterTabType.values();
        for (UpdateCenterTabType tabTitle : types)
        {
            args = new Bundle(args);
            args.putInt(KEY_PAGE, tabTitle.pageIndex);

            TitleTabView tabView = (TitleTabView) LayoutInflater.from(getActivity())
                    .inflate(R.layout.message_tab_item, mTabHost.getTabWidget(), false);
            String title = getString(tabTitle.titleRes, 0);
            tabView.setTitle(title);

            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(title).setIndicator(tabView);
            mTabHost.addTab(tabSpec, tabTitle.tabClass, args);
        }

        return mTabHost;
    }

    private void changeTabTitleNumber(UpdateCenterTabType tabType, int number)
    {
        TitleTabView tabView = (TitleTabView) mTabHost.getTabWidget().getChildAt(tabType.ordinal());
        tabView.setTitleNumber(number);
    }

    @Override public void onTitleNumberChanged(UpdateCenterTabType tabType, int number)
    {
        changeTabTitleNumber(tabType, number);
    }


    private class FetchUserProfileListener implements DTOCache.Listener<UserBaseKey,UserProfileDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THToast.show(new THException(error));
        }
    }

    private void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        if (andDisplay)
        {
            changeTabTitleNumber(UpdateCenterTabType.Messages, userProfileDTO.unreadMessageThreadsCount);
            changeTabTitleNumber(UpdateCenterTabType.Notifications, userProfileDTO.unreadNotificationsCount);
        }
    }
}
