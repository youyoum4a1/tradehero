package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.social.InviteDTO;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.SocialService;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.LinkedInUtils;
import com.tradehero.th.utils.StringUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.inject.Inject;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ReferralFragment extends DashboardFragment
{
    private static final int MIN_LENGTH_TEXT_TO_SEARCH = 0;
    private static final String TAG = ReferralFragment.class.getName();
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    @Inject protected Lazy<UserService> userService;
    @Inject protected Lazy<SocialService> socialService;
    @Inject protected Lazy<LinkedInUtils> linkedInUtils;
    @Inject protected Lazy<UserProfileCache> userProfileCache;

    @Inject protected Lazy<FacebookUtils> facebookUtils;
    private FriendListAdapter referFriendListAdapter;
    private ProgressDialog progressDialog;
    private View headerView;
    private Button inviteFriendButton;
    private TextView searchTextView;
    private StickyListHeadersListView stickyListHeadersListView;
    private SocialNetworkEnum currentSocialNetworkConnect;

    private List<UserFriendsDTO> selectedLinkedInFriends;
    private List<UserFriendsDTO> selectedFacebookFriends;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.refer_fragment, container, false);
        headerView = inflater.inflate(R.layout.refer_friend_header, null, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        inviteFriendButton = (Button) view.findViewById(R.id.refer_friend_invite_button);
        if (inviteFriendButton != null)
        {
            inviteFriendButton.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    inviteFriends();
                }
            });
        }

        referFriendListAdapter = createFriendListAdapter();
        stickyListHeadersListView = (StickyListHeadersListView) view.findViewById(R.id.sticky_list);

        View emptyView = view.findViewById(R.id.refer_friend_list_empty_view);
        if (emptyView != null)
        {
            //stickyListHeadersListView.getWrappedList().setEmptyView(emptyView);
        }

        if (stickyListHeadersListView.getHeaderViewsCount() == 0)
        {
            stickyListHeadersListView.addHeaderView(headerView);
        }

        stickyListHeadersListView.setAdapter(referFriendListAdapter);

        stickyListHeadersListView.getWrappedList().setClickable(true);
        stickyListHeadersListView.setOnItemClickListener(itemClickListener);

        searchTextView = (TextView) headerView.findViewById(R.id.invite_friend_search);
    }

    private void inviteFriends()
    {
        if (referFriendListAdapter != null)
        {
            selectedLinkedInFriends = referFriendListAdapter.getSelectedLinkedInFriends();
            if (selectedLinkedInFriends.size() > 0)
            {
                currentSocialNetworkConnect = SocialNetworkEnum.LI;
                linkedInUtils.get().logIn(getActivity(), socialNetworkCallback);
            }

            selectedFacebookFriends = referFriendListAdapter.getSelectedFacebookFriends();
            if (selectedFacebookFriends.size() > 0)
            {
                currentSocialNetworkConnect = SocialNetworkEnum.FB;
                facebookUtils.get().logIn(getActivity(), socialNetworkCallback);
            }
        }
    }

    @Override public void onPause()
    {
        if (searchTextView != null)
        {
            searchTextView.removeTextChangedListener(searchTextWatcher);
        }
        super.onPause();
    }

    @Override public void onDestroy()
    {
        stickyListHeadersListView.setOnItemClickListener(null);
        super.onDestroy();
    }

    private FriendListAdapter createFriendListAdapter()
    {
        return new FriendListAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.refer_friend_list_item_view);
    }

    @Override public void onResume()
    {
        super.onResume();

        resetSearchText();
        getProgressDialog().show();
        userService.get().getFriends(currentUserBaseKeyHolder.getCurrentUserBaseKey().key,
                getFriendsCallback);
    }

    private void resetSearchText()
    {
        if (searchTextView != null)
        {
            searchTextView.setText("");
            searchTextView.addTextChangedListener(searchTextWatcher);
        }
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
        if (referFriendListAdapter != null)
        {
            referFriendListAdapter.setItems(userFriendsDTOs);
            referFriendListAdapter.notifyDataSetChanged();
        }
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
            THToast.show(getString(R.string.retrieve_friends_failed));
        }
    };

    //<editor-fold desc="Handle item selection">
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            if (view instanceof UserFriendDTOView)
            {
                UserFriendDTOView userFriendDTOView = ((UserFriendDTOView) view);
                userFriendDTOView.toggle();
                checkIfAbleToSendInvitation();
            }
        }
    };

    private void checkIfAbleToSendInvitation()
    {
        if (referFriendListAdapter != null && referFriendListAdapter.getSelectedCount() != 0)
        {
            toggleInviteFriendButton(true);
        }
        else
        {
            toggleInviteFriendButton(false);
        }
    }

    private void toggleInviteFriendButton(boolean visibility)
    {
        if (inviteFriendButton != null)
        {
            inviteFriendButton.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }

    //</editor-fold>

    private TextWatcher searchTextWatcher = new TextWatcher()
    {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }

        @Override public void afterTextChanged(Editable s)
        {
            if (searchTextView != null)
            {
                final String newText = searchTextView.getText().toString();
                if (newText.length() > MIN_LENGTH_TEXT_TO_SEARCH)
                {
                    searchTextView.post(new Runnable()
                    {
                        @Override public void run()
                        {
                            activateSearch(newText);
                        }
                    });
                }
                else
                {
                    referFriendListAdapter.resetItems();
                }
            }
        }
    };

    private void activateSearch(String searchText)
    {
        THLog.d(TAG, "Search term: " + searchText + ", Thread: " + Looper.myLooper());
        referFriendListAdapter.filter(searchText);
        referFriendListAdapter.notifyDataSetChanged();
    }

    private THCallback<Response> inviteFriendCallback = new THCallback<Response>()
    {
        @Override protected void finish()
        {
            getProgressDialog().dismiss();
        }

        @Override protected void success(Response response, THResponse thResponse)
        {
            THToast.show(R.string.success);
        }

        @Override protected void failure(THException ex)
        {
            // TODO failed
        }
    };
    private LogInCallback socialNetworkCallback = new LogInCallback()
    {
        @Override public void done(UserBaseDTO user, THException ex)
        {
            getProgressDialog().dismiss();
        }

        @Override public boolean onSocialAuthDone(JSONObject json)
        {
            socialService.get().connect(
                    currentUserBaseKeyHolder.getCurrentUserBaseKey().key,
                    UserFormFactory.create(json),
                    createSocialConnectCallback());
            progressDialog.setMessage(String.format(getString(R.string.connecting_tradehero), currentSocialNetworkConnect.getName()));
            return false;
        }

        @Override public void onStart()
        {
            getProgressDialog().show();
        }
    };

    private THCallback<UserProfileDTO> createSocialConnectCallback()
    {
        return new SocialLinkingCallback();
    }

    //<editor-fold desc="Tab bar informer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    private class SocialLinkingCallback extends THCallback<UserProfileDTO>
    {

        @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
        {
            userProfileCache.get().put(currentUserBaseKeyHolder.getCurrentUserBaseKey(), userProfileDTO);
            sendInvitation();
        }

        @Override protected void failure(THException ex)
        {
            // user unlinked current authentication
            THToast.show(ex);
        }

        @Override protected void finish()
        {
            progressDialog.dismiss();
        }
    }

    private void sendInvitation()
    {
        if (currentSocialNetworkConnect != null)
        {
            switch (currentSocialNetworkConnect)
            {
                case LI:
                    if (selectedLinkedInFriends != null && !selectedLinkedInFriends.isEmpty())
                    {
                        InviteFormDTO inviteFriendForm = new InviteFormDTO();
                        inviteFriendForm.users = new ArrayList<>();
                        for (UserFriendsDTO userFriendsDTO : selectedLinkedInFriends)
                        {
                            InviteDTO inviteDTO = new InviteDTO();
                            inviteDTO.liId = userFriendsDTO.liId;
                            inviteFriendForm.users.add(inviteDTO);
                        }
                        getProgressDialog().show();
                        userService.get().inviteFriends(currentUserBaseKeyHolder.getCurrentUserBaseKey().key, inviteFriendForm, inviteFriendCallback);
                    }
                case FB:
                    if (selectedFacebookFriends != null && !selectedFacebookFriends.isEmpty())
                    {
                        sendRequestDialog();
                    }
            }
        }
    }

    private void sendRequestDialog()
    {
        String[] fbIds = new String[selectedFacebookFriends.size()];
        if (selectedFacebookFriends != null && !selectedFacebookFriends.isEmpty())
        {
            for (int i=0; i<selectedFacebookFriends.size(); ++i)
            {
                fbIds[i] = selectedFacebookFriends.get(i).fbId;
            }
        }

        Bundle params = new Bundle();
        params.putString("message", getString(R.string.facebook_tradehero_refer_friend_message));
        params.putString("to", StringUtils.join(",", fbIds));

        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(getActivity(),
                        Session.getActiveSession(),
                        params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener()
                {

                    @Override
                    public void onComplete(Bundle values,FacebookException error)
                    {
                        if (error != null)
                        {
                            if (error instanceof FacebookOperationCanceledException)
                            {
                                THToast.show("Request cancelled");
                            }
                            else
                            {
                                THToast.show("Network Error");
                            }
                        }
                        else
                        {
                            final String requestId = values.getString("request");
                            if (requestId != null)
                            {
                                THToast.show("Request sent");
                            }
                            else
                            {
                                THToast.show("Request cancelled");
                            }
                        }
                    }
                })
                .build();
        requestsDialog.show();
    }
}