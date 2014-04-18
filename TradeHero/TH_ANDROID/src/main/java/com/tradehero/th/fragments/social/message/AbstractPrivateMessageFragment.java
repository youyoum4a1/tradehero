package com.tradehero.th.fragments.social.message;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.fragments.discussion.AbstractDiscussionFragment;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.user.FollowUserAssistant;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import timber.log.Timber;

abstract public class AbstractPrivateMessageFragment extends AbstractDiscussionFragment
{
    private static final String CORRESPONDENT_USER_BASE_BUNDLE_KEY =
            AbstractPrivateMessageFragment.class.getName() + ".correspondentUserBaseKey";

    @Inject protected CurrentUserId currentUserId;
    @Inject protected UserBaseDTOUtil userBaseDTOUtil;
    @Inject protected Picasso picasso;
    @Inject @ForUserPhoto protected Transformation userPhotoTransformation;
    @Inject protected HeroAlertDialogUtil heroAlertDialogUtil;

    @Inject protected UserProfileCache userProfileCache;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileCacheTask;
    protected UserBaseKey correspondentId;
    protected UserProfileDTO correspondentProfile;

    @Inject protected UserMessagingRelationshipCache userMessagingRelationshipCache;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserMessagingRelationshipDTO>
            messagingRelationshipCacheTask;
    protected UserMessagingRelationshipDTO userMessagingRelationshipDTO;

    protected ImageView correspondentImage;
    @InjectView(R.id.private_message_empty) protected TextView emptyHint;
    @InjectView(R.id.post_comment_action_submit) protected TextView buttonSend;
    @InjectView(R.id.post_comment_text) protected EditText messageToSend;
    @InjectView(R.id.private_message_status_container) protected View statusViewContainer;
    @InjectView(R.id.private_message_status_text) protected TextView statusViewText;

    public static void putCorrespondentUserBaseKey(Bundle args, UserBaseKey correspondentBaseKey)
    {
        args.putBundle(CORRESPONDENT_USER_BASE_BUNDLE_KEY, correspondentBaseKey.getArgs());
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        correspondentId =
                new UserBaseKey(getArguments().getBundle(CORRESPONDENT_USER_BASE_BUNDLE_KEY));
    }

    protected DTOCache.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new AbstractPrivateMessageFragmentUserProfileListener();
    }

    protected DTOCache.Listener<UserBaseKey, UserMessagingRelationshipDTO> createMessageStatusCacheListener()
    {
        return new AbstractPrivateMessageFragmentMessageStatusListener();
    }

    @Override protected FollowUserAssistant.OnUserFollowedListener createUserFollowedListener()
    {
        return new AbstractPrivateMessageFragmentUserFollowedListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_private_message, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    @Override protected void initViews(View view)
    {
        messageToSend.setHint(R.string.private_message_message_hint);
        buttonSend.setText(R.string.private_message_btn_send);
        display();
        if (discussionView != null)
        {
            ((PrivateDiscussionView) discussionView).setMessageType(MessageType.PRIVATE);
            ((PrivateDiscussionView) discussionView).setMessageNotAllowedToSendListener(
                    new AbstractPrivateMessageFragmentOnMessageNotAllowedToSendListener());
            ((PrivateDiscussionView) discussionView).setUserMessagingRelationshipDTO(
                    userMessagingRelationshipDTO);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.private_message_menu, menu);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setSubtitle(R.string.private_message_subtitle);

        correspondentImage = (ImageView) menu.findItem(R.id.correspondent_picture);
        displayTitle();
        displayCorrespondentImage();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchCorrespondentProfile();
        fetchMessageStatus();
    }

    @Override public void onDestroyOptionsMenu()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        detachUserProfileTask();
        detachMessageStatusTask();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    private void detachUserProfileTask()
    {
        if (userProfileCacheTask != null)
        {
            userProfileCacheTask.setListener(null);
        }
        userProfileCacheTask = null;
    }

    private void detachMessageStatusTask()
    {
        if (messagingRelationshipCacheTask != null)
        {
            messagingRelationshipCacheTask.setListener(null);
        }
        messagingRelationshipCacheTask = null;
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    private void fetchCorrespondentProfile()
    {
        Timber.d("fetchCorrespondentProfile");
        detachUserProfileTask();
        userProfileCacheTask =
                userProfileCache.getOrFetch(correspondentId, createUserProfileCacheListener());
        userProfileCacheTask.execute();
    }

    private void fetchMessageStatus()
    {
        fetchMessageStatus(false);
    }

    private void fetchMessageStatus(boolean force)
    {
        Timber.d("fetchMessageStatus");
        detachMessageStatusTask();
        messagingRelationshipCacheTask =
                userMessagingRelationshipCache.getOrFetch(correspondentId, force,
                        createMessageStatusCacheListener());
        messagingRelationshipCacheTask.execute();
    }

    public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        Timber.d("userProfile %s", userProfileDTO);
        correspondentProfile = userProfileDTO;
        if (andDisplay)
        {
            displayCorrespondentImage();
            displayTitle();
            getSherlockActivity().invalidateOptionsMenu();
        }
    }

    public void linkWith(UserMessagingRelationshipDTO messageStatusDTO, boolean andDisplay)
    {
        this.userMessagingRelationshipDTO = messageStatusDTO;
        if (discussionView != null && discussionView instanceof PrivateDiscussionView)
        {
            ((PrivateDiscussionView) discussionView).setUserMessagingRelationshipDTO(
                    messageStatusDTO);
        }
        //TODO
        if (andDisplay)
        {
            displayMessagingStatusContainer();
            displayMessagingStatusText();
        }
    }

    public void display()
    {
        displayCorrespondentImage();
        displayTitle();
        displayMessagingStatusContainer();
        displayMessagingStatusText();
    }

    protected void displayCorrespondentImage()
    {
        if (correspondentImage != null)
        {
            RequestCreator picassoRequestCreator;
            if (correspondentProfile != null
                    && correspondentProfile.picture != null
                    && !correspondentProfile.picture.isEmpty())
            {
                picassoRequestCreator = picasso.load(correspondentProfile.picture);
            }
            else
            {
                picassoRequestCreator = picasso.load(R.drawable.superman_facebook);
            }
            picassoRequestCreator.transform(userPhotoTransformation)
                    .into(correspondentImage);
        }
    }

    protected void displayTitle()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        if (correspondentProfile != null)
        {
            String title =
                    userBaseDTOUtil.getLongDisplayName(getSherlockActivity(), correspondentProfile);
            Timber.d("Display title " + title);
            actionBar.setTitle(title);
        }
        else
        {
            actionBar.setTitle(R.string.loading_loading);
        }
    }

    protected void displayMessagingStatusContainer()
    {
        if (statusViewContainer != null)
        {
            // TODO better test
            statusViewContainer.setVisibility(
                    userMessagingRelationshipDTO == null
                            || userMessagingRelationshipDTO.canSendPrivate()
                            ? View.GONE : View.VISIBLE);
        }
    }

    protected void displayMessagingStatusText()
    {
        if (statusViewText != null)
        {
            if (userMessagingRelationshipDTO != null && userMessagingRelationshipDTO.isUnlimited())
            {
                statusViewText.setVisibility(View.GONE);
            }
            else if (userMessagingRelationshipDTO != null)
            {
                statusViewText.setVisibility(View.VISIBLE);
                statusViewText.setText(
                        getResources().getString(R.string.private_message_limited_count,
                                userMessagingRelationshipDTO.freeSendsRemaining));
            }
            else
            {
                statusViewText.setVisibility(View.GONE);
            }
        }
    }

    protected void showNotEnoughMessagesLeftDialog()
    {
        String heroName = userBaseDTOUtil.getLongDisplayName(getActivity(), correspondentProfile);
        heroAlertDialogUtil.popAlertNoMoreMessageFollow(
                getActivity(),
                new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialog, int which)
                    {
                        showPaidFollow();
                    }
                },
                heroName);
    }

    @OnClick(R.id.private_message_status_container) protected void showPaidFollow()
    {
        cancelOthersAndShowProductDetailList(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS);
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    protected class AbstractPrivateMessageFragmentUserProfileListener
            implements DTOCache.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override
        public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            Timber.e(error, "");
        }
    }

    protected class AbstractPrivateMessageFragmentMessageStatusListener
            implements DTOCache.Listener<UserBaseKey, UserMessagingRelationshipDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, UserMessagingRelationshipDTO value,
                boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            Timber.e(error, "");
        }
    }

    protected class AbstractPrivateMessageFragmentOnMessageNotAllowedToSendListener
            implements PrivatePostCommentView.OnMessageNotAllowedToSendListener
    {
        @Override public void onMessageNotAllowedToSend()
        {
            showNotEnoughMessagesLeftDialog();
        }
    }

    protected class AbstractPrivateMessageFragmentUserFollowedListener
            extends BasePurchaseManagerUserFollowedListener
    {
        @Override public void onUserFollowSuccess(UserBaseKey userFollowed,
                UserProfileDTO currentUserProfileDTO)
        {
            super.onUserFollowSuccess(userFollowed, currentUserProfileDTO);
            fetchMessageStatus(true);
        }

        @Override public void onUserFollowFailed(UserBaseKey userFollowed, Throwable error)
        {
            super.onUserFollowFailed(userFollowed, error);
        }
    }
}
