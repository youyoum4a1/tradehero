package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserService;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ReferralFragment extends DashboardFragment
{
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    @Inject protected Lazy<UserService> userService;

    private StickyListHeadersAdapter stickyListHeadersAdapter;
    private ProgressDialog progressDialog;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.refer_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        //stickyListHeadersAdapter = new
        StickyListHeadersListView stickyListHeadersListView = (StickyListHeadersListView) view.findViewById(R.id.sticky_list);
        stickyListHeadersListView.setAdapter(stickyListHeadersAdapter);

        View emptyView = view.findViewById(R.id.refer_friend_list_empty_view);
        if (emptyView != null)
        {
            stickyListHeadersListView.getWrappedList().setEmptyView(emptyView);
        }
    }

    @Override public void onResume()
    {
        super.onResume();

        getProgressDialog().show();
        userService.get().getFriends(currentUserBaseKeyHolder.getCurrentUserBaseKey().key,
                getFriendsCallback);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setTitle(getString(R.string.invite_friends));

        super.onCreateOptionsMenu(menu, inflater);
    }

    private ProgressDialog getProgressDialog()
    {
        if (progressDialog != null)
        {
            return progressDialog;
        }
        progressDialog = ProgressDialog.show(
                getActivity(),
                getString(R.string.loading_loading),
                getString(R.string.please_wait), true);
        progressDialog.hide();
        return progressDialog;
    }

    private void handleFriendListReceived(List<UserFriendsDTO> userFriendsDTOs)
    {

    }

    private THCallback<List<UserFriendsDTO>> getFriendsCallback = new THCallback<List<UserFriendsDTO>>()
    {
        @Override protected void finish()
        {
            getProgressDialog().dismiss();
        }

        @Override protected void success(List<UserFriendsDTO> userFriendsDTOs, THResponse thResponse)
        {
            handleFriendListReceived(userFriendsDTOs);
        }

        @Override protected void failure(THException ex)
        {

        }
    };

    //<editor-fold desc="Tab bar informer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}