package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
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
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.loaders.FriendListLoader;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.user.MiddleCallbackUpdateUserProfile;
import com.tradehero.th.network.service.SocialService;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.LinkedInUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.json.JSONObject;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class InviteFriendFragment extends DashboardFragment
{
    private static final String TAG = InviteFriendFragment.class.getName();

    private static final int MIN_LENGTH_TEXT_TO_SEARCH = 0;
    private static final int MAX_FACEBOOK_MESSAGE_LENGTH = 60;
    private static final int MAX_FACEBOOK_FRIENDS_RECEIVERS = 50;
    private static final int CONTACT_LOADER_ID = 0;

    @Inject protected CurrentUserId currentUserId;
    @Inject protected Lazy<UserService> userService;
    @Inject SocialServiceWrapper socialServiceWrapper;
    @Inject protected Lazy<SocialService> socialService;
    @Inject protected Lazy<LinkedInUtils> linkedInUtils;
    @Inject protected Lazy<UserProfileCache> userProfileCache;

    @Inject protected Lazy<FacebookUtils> facebookUtils;
    private FriendListAdapter referFriendListAdapter;
    private ProgressDialog progressDialog;
    private View headerView;
    private TextView inviteFriendButton;
    private TextView searchTextView;
    private StickyListHeadersListView stickyListHeadersListView;
    private SocialNetworkEnum currentSocialNetworkConnect;

    private List<UserFriendsDTO> selectedLinkedInFriends;
    private List<UserFriendsDTO> selectedFacebookFriends;

    private ToggleButton fbToggle;
    private ToggleButton liToggle;
    private ToggleButton contactToggle;

    private MiddleCallbackUpdateUserProfile middleCallbackConnect;

    private LoaderManager.LoaderCallbacks<List<UserFriendsDTO>> contactListLoaderCallback;
    private THCallback<Response> inviteFriendCallback;
    private LogInCallback socialNetworkCallback;
    private TextWatcher searchTextWatcher;
    private AdapterView.OnItemClickListener itemClickListener;
    private CompoundButton.OnCheckedChangeListener onToggleFriendListListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        contactListLoaderCallback = new LoaderManager.LoaderCallbacks<List<UserFriendsDTO>>()
        {
            @Override public Loader<List<UserFriendsDTO>> onCreateLoader(int id, Bundle args)
            {
                return new FriendListLoader(getActivity());
            }

            @Override public void onLoadFinished(Loader<List<UserFriendsDTO>> loader, List<UserFriendsDTO> userFriendsDTOs)
            {
                getProgressDialog().dismiss();
                handleFriendListReceived(userFriendsDTOs);
            }

            @Override public void onLoaderReset(Loader<List<UserFriendsDTO>> loader)
            {

            }
        };
        inviteFriendCallback = new THCallback<Response>()
        {
            @Override protected void finish()
            {
                if (progressDialog != null)
                {
                    progressDialog.dismiss();
                }
                conditionalSendInvitations();
            }

            @Override protected void success(Response response, THResponse thResponse)
            {
                THToast.show(R.string.invite_friend_success);
                // just hacked it :))
            }

            @Override protected void failure(THException ex)
            {
                // TODO failed
            }
        };
        socialNetworkCallback = new LogInCallback()
        {
            @Override public void done(UserBaseDTO user, THException ex)
            {
                if (!isDetached())
                {
                    getProgressDialog().dismiss();
                }
            }

            @Override public boolean onSocialAuthDone(JSONObject json)
            {
                detachMiddleCallbackConnect();
                middleCallbackConnect = socialServiceWrapper.connect(
                        currentUserId.toUserBaseKey(),
                        UserFormFactory.create(json),
                        createSocialConnectCallback());
                if (!isDetached())
                {
                    progressDialog.setMessage(String.format(getString(R.string.authentication_connecting_tradehero), currentSocialNetworkConnect.getName()));
                }
                return false;
            }

            @Override public void onStart()
            {
                if (!isDetached())
                {
                    getProgressDialog().show();
                }
            }
        };
        searchTextWatcher = new TextWatcher()
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
                    else if (referFriendListAdapter != null)
                    {
                        referFriendListAdapter.resetItems();
                    }
                }
            }
        };
        itemClickListener = new AdapterView.OnItemClickListener()
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
        onToggleFriendListListener = new CompoundButton.OnCheckedChangeListener()
        {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (buttonView != null && referFriendListAdapter != null)
                {
                    switch (buttonView.getId())
                    {
                        case R.id.invite_friend_contact_toggle:
                            referFriendListAdapter.toggleContactSelection(isChecked);
                            break;
                        case R.id.invite_friend_facebook_toggle:
                            referFriendListAdapter.toggleFacebookSelection(isChecked);
                            break;
                        case R.id.invite_friend_linkedin_toggle:
                            referFriendListAdapter.toggleLinkedInSelection(isChecked);
                            break;
                    }
                    checkIfAbleToSendInvitation();
                    referFriendListAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.invite_friend_content, container, false);
        headerView = inflater.inflate(R.layout.invite_friend_header, null, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        inviteFriendButton = (TextView) view.findViewById(R.id.refer_friend_invite_button);
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

        View emptyView = view.findViewById(R.id.friend_list_empty_view);
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
        if (searchTextView != null)
        {
            searchTextView.addTextChangedListener(searchTextWatcher);
        }
        fbToggle = (ToggleButton) headerView.findViewById(R.id.invite_friend_facebook_toggle);
        liToggle = (ToggleButton) headerView.findViewById(R.id.invite_friend_linkedin_toggle);
        contactToggle = (ToggleButton) headerView.findViewById(R.id.invite_friend_contact_toggle);

        if (fbToggle != null)
        {
            fbToggle.setOnCheckedChangeListener(onToggleFriendListListener);
        }
        if (liToggle != null)
        {
            liToggle.setOnCheckedChangeListener(onToggleFriendListListener);
        }
        if (contactToggle != null)
        {
            contactToggle.setOnCheckedChangeListener(onToggleFriendListListener);
        }
    }

    @Override public void onResume()
    {
        super.onResume();

        getProgressDialog().show();

        // load friend list from server side
        // userService.get().getFriends(currentUserId.get(), getFriendsCallback);
        // load contact (with email) list of the phone
        getLoaderManager().initLoader(CONTACT_LOADER_ID, null, contactListLoaderCallback);
    }

    @Override public void onDestroyView()
    {
        detachMiddleCallbackConnect();
        if (searchTextView != null)
        {
            searchTextView.removeTextChangedListener(searchTextWatcher);
        }
        searchTextView = null;

        if (stickyListHeadersListView != null)
        {
            stickyListHeadersListView.setOnItemClickListener(null);
        }
        stickyListHeadersListView = null;

        if (fbToggle != null)
        {
            fbToggle.setOnCheckedChangeListener(null);
        }
        fbToggle = null;

        if (liToggle != null)
        {
            liToggle.setOnCheckedChangeListener(null);
        }
        liToggle = null;

        if (contactToggle != null)
        {
            contactToggle.setOnCheckedChangeListener(null);
        }
        contactToggle = null;

        if (inviteFriendButton != null)
        {
            inviteFriendButton.setOnClickListener(null);
        }
        inviteFriendButton = null;

        referFriendListAdapter = null;
        headerView = null;

        LoaderManager loaderManager = getLoaderManager();
        if (loaderManager != null)
        {
            loaderManager.destroyLoader(CONTACT_LOADER_ID);
        }

        super.onDestroyView();
    }

    protected void detachMiddleCallbackConnect()
    {
        if (middleCallbackConnect != null)
        {
            middleCallbackConnect.setPrimaryCallback(null);
        }
        middleCallbackConnect = null;
    }

    @Override public void onDestroy()
    {
        contactListLoaderCallback = null;
        inviteFriendCallback = null;
        socialNetworkCallback = null;
        searchTextWatcher = null;
        itemClickListener = null;
        onToggleFriendListListener = null;

        super.onDestroy();
    }

    private void inviteFriends()
    {
        if (referFriendListAdapter != null)
        {
            selectedLinkedInFriends = referFriendListAdapter.getSelectedLinkedInFriends();
            selectedFacebookFriends = referFriendListAdapter.getSelectedFacebookFriends();
            sendEmailInvitation(referFriendListAdapter.getSelectedContacts());
        }
    }

    private void sendEmailInvitation(List<UserFriendsDTO> selectedContacts)
    {
        if (selectedContacts != null && !selectedContacts.isEmpty())
        {
            InviteFormDTO inviteFriendForm = new InviteFormDTO();
            inviteFriendForm.users = new ArrayList<>();
            for (UserFriendsDTO userFriendsDTO : selectedContacts)
            {
                InviteDTO inviteDTO = new InviteDTO();
                inviteDTO.email = userFriendsDTO.getEmail();
                inviteFriendForm.users.add(inviteDTO);
            }
            //getProgressDialog().setMessage(getString(R.string.sending_email_invitation));
            getProgressDialog().show();
            userService.get().inviteFriends(currentUserId.get(), inviteFriendForm, inviteFriendCallback);
        }
        else
        {
            conditionalSendInvitations();
        }
    }

    private FriendListAdapter createFriendListAdapter()
    {
        return new FriendListAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.refer_friend_list_item_view);
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
        progressDialog = ProgressDialogUtil.show(
                getActivity(),
                R.string.loading_loading,
                R.string.alert_dialog_please_wait);
        progressDialog.hide();
        return progressDialog;
    }

    private void handleFriendListReceived(List<UserFriendsDTO> userFriendsDTOs)
    {
        if (referFriendListAdapter != null)
        {
            referFriendListAdapter.setItems(userFriendsDTOs);
            referFriendListAdapter.notifyDataSetChanged();

            if (searchTextView != null)
            {
                searchTextView.setText("");
            }
        }
    }

    //<editor-fold desc="Handle item selection">
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

    //<editor-fold desc="Search">
    private void activateSearch(String searchText)
    {
        THLog.d(TAG, "Search term: " + searchText + ", Thread: " + Looper.myLooper());
        if (referFriendListAdapter != null)
        {
            referFriendListAdapter.filter(searchText);
            referFriendListAdapter.notifyDataSetChanged();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Tab bar informer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Callback for authentication & rest service">
    private void conditionalSendInvitations()
    {
        // make sure that this fragment is added to an activity (getActivity() != null)
        if (isAdded())
        {
            if (selectedLinkedInFriends != null && !selectedLinkedInFriends.isEmpty())
            {
                currentSocialNetworkConnect = SocialNetworkEnum.LI;

                getProgressDialog().show();
                linkedInUtils.get().logIn(getActivity(), socialNetworkCallback);
            }
            else if (selectedFacebookFriends != null && !selectedFacebookFriends.isEmpty())
            {
                currentSocialNetworkConnect = SocialNetworkEnum.FB;
                facebookUtils.get().logIn(getActivity(), socialNetworkCallback);
            }
        }
    }

    private THCallback<UserProfileDTO> createSocialConnectCallback()
    {
        return new SocialLinkingCallback();
    }

    private class SocialLinkingCallback extends THCallback<UserProfileDTO>
    {
        @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
        {
            userProfileCache.get().put(currentUserId.toUserBaseKey(), userProfileDTO);
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
    //</editor-fold>

    //<editor-fold desc="Sending actions">
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
                        selectedLinkedInFriends = null;
                        getProgressDialog().show();
                        userService.get().inviteFriends(currentUserId.get(), inviteFriendForm, inviteFriendCallback);
                    }
                case FB:
                    if (Session.getActiveSession() == null)
                    {
                        conditionalSendInvitations();
                        return;
                    }
                    if (selectedFacebookFriends != null && !selectedFacebookFriends.isEmpty())
                    {
                        sendRequestDialog();
                    }
            }
        }
    }

    private void sendRequestDialog()
    {
        StringBuilder stringBuilder = new StringBuilder();
        if (selectedFacebookFriends != null && !selectedFacebookFriends.isEmpty())
        {
            for (int i = 0; i < selectedFacebookFriends.size() && i < MAX_FACEBOOK_FRIENDS_RECEIVERS; ++i)
            {
                stringBuilder.append(selectedFacebookFriends.get(i).fbId).append(',');
            }
        }
        // disable loop
        selectedFacebookFriends = null;
        // remove the last comma
        if (stringBuilder.length() > 0)
        {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        THLog.d(TAG, "list of fbIds: " + stringBuilder.toString());

        Bundle params = new Bundle();
        String messageToFacebookFriends = getString(R.string.invite_friend_facebook_tradehero_refer_friend_message);
        if (messageToFacebookFriends.length() > MAX_FACEBOOK_MESSAGE_LENGTH)
        {
            messageToFacebookFriends = messageToFacebookFriends.substring(0, MAX_FACEBOOK_MESSAGE_LENGTH);
        }

        params.putString("message", messageToFacebookFriends);
        params.putString("to", stringBuilder.toString());

        WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(getActivity(), Session.getActiveSession(), params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener()
                {

                    @Override
                    public void onComplete(Bundle values, FacebookException error)
                    {
                        if (error != null)
                        {
                            if (error instanceof FacebookOperationCanceledException)
                            {
                                THToast.show(R.string.invite_friend_request_canceled);
                            }
                        }
                        else
                        {
                            final String requestId = values.getString("request");
                            if (requestId != null)
                            {
                                THToast.show(R.string.invite_friend_request_sent);
                            }
                            else
                            {
                                THToast.show(R.string.invite_friend_request_canceled);
                            }
                        }
                    }
                })
                .build();
        requestsDialog.show();
    }
    //</editor-fold>
}