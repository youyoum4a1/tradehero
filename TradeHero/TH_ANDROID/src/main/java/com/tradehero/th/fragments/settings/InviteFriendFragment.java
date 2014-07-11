package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.tradehero.common.utils.THToast;
import com.tradehero.thm.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.social.InviteContactEntryDTO;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsFacebookDTO;
import com.tradehero.th.api.social.UserFriendsLinkedinDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.loaders.FriendListLoader;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SocialService;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.LinkedInUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import timber.log.Timber;

public class InviteFriendFragment extends DashboardFragment
{
    private static final int MIN_LENGTH_TEXT_TO_SEARCH = 0;
    private static final int MAX_FACEBOOK_MESSAGE_LENGTH = 60;
    private static final int MAX_FACEBOOK_FRIENDS_RECEIVERS = 50;
    private static final int CONTACT_LOADER_ID = 0;

    @Inject SocialServiceWrapper socialServiceWrapper;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject Lazy<SocialService> socialService;
    @Inject Lazy<LinkedInUtils> linkedInUtils;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<FacebookUtils> facebookUtils;
    @Inject THLocalyticsSession localyticsSession;
    @Inject ProgressDialogUtil progressDialogUtil;

    private FriendListAdapter referFriendListAdapter;
    private ProgressDialog progressDialog;
    private View headerView;
    @InjectView(R.id.refer_friend_invite_button) TextView inviteFriendButton;
    private TextView searchTextView;
    @InjectView(R.id.sticky_list) StickyListHeadersListView stickyListHeadersListView;
    private SocialNetworkEnum currentSocialNetworkConnect;

    private List<UserFriendsLinkedinDTO> selectedLinkedInFriends;
    private List<UserFriendsFacebookDTO> selectedFacebookFriends;

    private ToggleButton fbToggle;
    private ToggleButton liToggle;
    private ToggleButton contactToggle;

    private MiddleCallback<UserProfileDTO> middleCallbackConnect;
    private MiddleCallback<Response> middleCallbackInvite;

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
            @Override public void done(UserLoginDTO user, THException ex)
            {
                if (!isDetached())
                {
                    getProgressDialog().dismiss();
                }
            }

            @Override public boolean onSocialAuthDone(JSONCredentials json)
            {
                detachMiddleCallbackConnect();
                middleCallbackConnect = socialServiceWrapper.connect(
                        currentUserId.toUserBaseKey(),
                        UserFormFactory.create(json),
                        createSocialConnectCallback());
                FragmentActivity activity = getActivity();
                if (!isDetached() && activity != null && !activity.isFinishing())
                {
                    progressDialog.setMessage(getString(
                            R.string.authentication_connecting_tradehero,
                            currentSocialNetworkConnect.getName()));
                }
                return true;
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
        ButterKnife.inject(this, view);
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
        setToggleButtonsEnabled(false);
    }

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.Referrals_Settings);

        getProgressDialog().show();

        // load friend list from server side
        // userServiceWrapper.get().getFriends(currentUserId.get(), getFriendsCallback);
        // load contact (with email) list of the phone
        getLoaderManager().initLoader(CONTACT_LOADER_ID, null, contactListLoaderCallback);
    }

    @Override public void onDestroyView()
    {
        detachMiddleCallbackConnect();
        detachMiddleCallbackInvite();
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

    protected void detachMiddleCallbackInvite()
    {
        if (middleCallbackInvite != null)
        {
            middleCallbackInvite.setPrimaryCallback(null);
        }
        middleCallbackInvite = null;
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
                InviteContactEntryDTO inviteDTO = new InviteContactEntryDTO();
                inviteDTO.email = userFriendsDTO.email;
                inviteFriendForm.users.add(inviteDTO);
            }
            getProgressDialog().show();
            detachMiddleCallbackInvite();
            middleCallbackInvite = userServiceWrapper.get().inviteFriends(currentUserId.toUserBaseKey(), inviteFriendForm, inviteFriendCallback);
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
        setActionBarTitle(getString(R.string.invite_friends));
        super.onCreateOptionsMenu(menu, inflater);
    }

    private ProgressDialog getProgressDialog()
    {
        if (progressDialog != null)
        {
            return progressDialog;
        }
        progressDialog = progressDialogUtil.show(
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

            setToggleButtonsEnabled(userFriendsDTOs != null);
        }
    }

    private void setToggleButtonsEnabled(boolean enabled)
    {
        if (fbToggle != null)
        {
            fbToggle.setEnabled(enabled);
        }
        if (liToggle != null)
        {
            liToggle.setEnabled(enabled);
        }
        if (contactToggle != null)
        {
            contactToggle.setEnabled(enabled);
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
        if (referFriendListAdapter != null)
        {
            referFriendListAdapter.filter(searchText);
            referFriendListAdapter.notifyDataSetChanged();
        }
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
                currentSocialNetworkConnect = SocialNetworkEnum.LN;

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
                case LN:
                    if (selectedLinkedInFriends != null && !selectedLinkedInFriends.isEmpty())
                    {
                        InviteFormDTO inviteFriendForm = new InviteFormDTO();
                        inviteFriendForm.users = new ArrayList<>();
                        for (UserFriendsLinkedinDTO userFriendsDTO : selectedLinkedInFriends)
                        {
                            inviteFriendForm.users.add(userFriendsDTO.createInvite());
                        }
                        selectedLinkedInFriends = null;
                        getProgressDialog().show();
                        detachMiddleCallbackInvite();
                        middleCallbackInvite = userServiceWrapper.get().inviteFriends(currentUserId.toUserBaseKey(), inviteFriendForm, inviteFriendCallback);
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
            Collections.shuffle(selectedFacebookFriends);
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
        Timber.d("list of fbIds: %s", stringBuilder.toString());

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