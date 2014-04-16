package com.tradehero.th.fragments.social.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTOList;
import com.tradehero.th.api.discussion.MessageHeaderIdList;
import com.tradehero.th.api.discussion.MessageStatusDTO;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.DiscussionListKeyFactory;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.discussion.key.RecipientTypedMessageListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.fragments.discussion.AbstractDiscussionFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.user.FollowUserAssistant;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCache;
import com.tradehero.th.persistence.discussion.MessageStatusCache;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import java.util.Collection;
import javax.inject.Inject;
import timber.log.Timber;

public class PrivateMessageFragment extends AbstractDiscussionFragment
{
    public static final String CORRESPONDENT_USER_BASE_BUNDLE_KEY = PrivateMessageFragment.class.getName() + ".correspondentUserBaseKey";
    public static final String DISCUSSION_LIST_KEY_BUNDLE_KEY = PrivateMessageFragment.class.getName() + ".discussionListKey";
    public static final int DEFAULT_MAX_COUNT = 10;

    @Inject CurrentUserId currentUserId;
    @Inject UserBaseDTOUtil userBaseDTOUtil;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;
    ImageView correspondentImage;

    @Inject UserProfileCache userProfileCache;
    private DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileCacheTask;
    private UserBaseKey correspondentId;
    private UserProfileDTO correspondentProfile;

    @Inject MessageStatusCache messageStatusCache;
    private DTOCache.Listener<UserBaseKey, MessageStatusDTO> messageStatusCacheListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, MessageStatusDTO> messageStatusCacheTask;
    private MessageStatusDTO messageStatusDTO;

    private RecipientTypedMessageListKey nextLoadingMessageKey;
    @Inject MessageHeaderCache messageHeaderCache;
    @Inject MessageHeaderListCache messageHeaderListCache;
    private DTOCache.Listener<MessageListKey, MessageHeaderIdList> messageHeaderListCacheListener;
    private DTOCache.GetOrFetchTask<MessageListKey, MessageHeaderIdList> messageHeaderListCacheTask;
    private MessageHeaderDTOList loadedMessages;
    private MessageHeaderDTO currentMessageHeader;

    @Inject DiscussionCache discussionCache;
    @Inject DiscussionListCache discussionListCache;
    private DTOCache.Listener<DiscussionListKey, DiscussionKeyList> discussionListCacheListener;
    private DTOCache.GetOrFetchTask<DiscussionListKey, DiscussionKeyList> discussionListCacheTask;
    @Inject DiscussionListKeyFactory discussionListKeyFactory;
    private DiscussionListKey nextDiscussionListKey;
    private DiscussionDTOList loadedDiscussions;

    @InjectView(R.id.private_message_empty) TextView emptyHint;
    @InjectView(R.id.post_comment_action_submit) TextView buttonSend;
    @InjectView(R.id.post_comment_text) EditText messageToSend;
    @InjectView(R.id.private_message_status_container) View statusViewContainer;
    @InjectView(R.id.private_message_status_text) TextView statusViewText;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        correspondentId = new UserBaseKey(getArguments().getBundle(CORRESPONDENT_USER_BASE_BUNDLE_KEY));
        userProfileCacheListener = new PrivateMessageFragmentUserProfileListener();
        messageStatusCacheListener = new PrivateMessageFragmentMessageStatusListener();
        messageHeaderListCacheListener = new PrivateMessageFragmentMessageListListener();
        discussionListCacheListener = new PrivateMessageFragmentDiscussionListListener();
        //nextLoadingMessageKey = new RecipientTypedMessageListKey(MessageListKey.FIRST_PAGE, 10, DiscussionType.PRIVATE_MESSAGE, correspondentId);
        nextLoadingMessageKey = new RecipientTypedMessageListKey(MessageListKey.FIRST_PAGE, 10, DiscussionType.PRIVATE_MESSAGE, currentUserId.toUserBaseKey());
        loadedMessages = new MessageHeaderDTOList();
        loadedDiscussions = new DiscussionDTOList();
    }

    @Override protected FollowUserAssistant.OnUserFollowedListener createUserFollowedListener()
    {
        return new PrivateMessageFragmentUserFollowedListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
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
        //postCommentView.setCommentPostedListener(new PrivateMessageFragmentCommentPostedListener());
        messageToSend.setHint(R.string.private_message_message_hint);
        buttonSend.setText(R.string.private_message_btn_send);
        display();
        ((PrivateDiscussionView) discussionView).setMessageType(MessageType.PRIVATE);
        ((PrivateDiscussionView) discussionView).setMessageNotAllowedToSendListener(new PrivateMessageFragmentOnMessageNotAllowedToSendListener());
        ((PrivateDiscussionView) discussionView).setMessageStatusDTO(messageStatusDTO);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.private_message_menu, menu);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_SHOW_HOME);

        correspondentImage = (ImageView) menu.findItem(R.id.correspondent_picture);
        displayTitle();
        displayCorrespondentImage();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();
        nextDiscussionListKey = discussionListKeyFactory.create(getArguments().getBundle(DISCUSSION_LIST_KEY_BUNDLE_KEY));
        fetchCorrespondentProfile();
        fetchMessageStatus();
        fetchMessageList();
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
        detachMessageListTask();
        detachDiscussionListTask();

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
        if (messageStatusCacheTask != null)
        {
            messageStatusCacheTask.setListener(null);
        }
        messageStatusCacheTask = null;
    }

    private void detachMessageListTask()
    {
        if (messageHeaderListCacheTask != null)
        {
            messageHeaderListCacheTask.setListener(null);
        }
        messageHeaderListCacheTask = null;
    }

    private void detachDiscussionListTask()
    {
        if (discussionListCacheTask != null)
        {
            discussionListCacheTask.setListener(null);
        }
        discussionListCacheTask = null;
    }

    @Override public void onDestroy()
    {
        userProfileCacheListener = null;
        messageStatusCacheListener = null;
        messageHeaderListCacheListener = null;
        discussionListCacheListener = null;
        super.onDestroy();
    }

    private void fetchCorrespondentProfile()
    {
        Timber.d("fetchCorrespondentProfile");
        detachUserProfileTask();
        userProfileCacheTask = userProfileCache.getOrFetch(correspondentId, userProfileCacheListener);
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
        messageStatusCacheTask = messageStatusCache.getOrFetch(correspondentId, force, messageStatusCacheListener);
        messageStatusCacheTask.execute();
    }

    private void fetchMessageList()
    {
        Timber.d("fetchMessageList");
        detachMessageListTask();
        messageHeaderListCacheTask = messageHeaderListCache.getOrFetch(nextLoadingMessageKey, messageHeaderListCacheListener);
        messageHeaderListCacheTask.execute();
    }

    private void setNextMessageListKey()
    {
        nextLoadingMessageKey = nextLoadingMessageKey.next();
    }

    private void fetchDiscussionList()
    {
        Timber.d("fetchDiscussionList");
        detachDiscussionListTask();
        discussionListCacheTask = discussionListCache.getOrFetch(nextDiscussionListKey, discussionListCacheListener);
        discussionListCacheTask.execute();
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

    @Override protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        super.linkWith(discussionKey, andDisplay);
        this.nextDiscussionListKey = new DiscussionListKey(discussionKey.getType(), discussionKey.id);
        fetchDiscussionList();
    }

    public void linkWith(MessageStatusDTO messageStatusDTO, boolean andDisplay)
    {
        this.messageStatusDTO = messageStatusDTO;
        if (discussionView != null && discussionView instanceof PrivateDiscussionView)
        {
            ((PrivateDiscussionView) discussionView).setMessageStatusDTO(messageStatusDTO);
        }
        //TODO
        if (andDisplay)
        {
            displayMessagingStatusContainer();
            displayMessagingStatusText();
        }
    }

    public void linkWith(Collection<MessageHeaderDTO> messageHeaders, boolean andDisplay)
    {
        Timber.d("messageHeaders size %d", messageHeaders.size());
        loadedMessages.addAll(messageHeaders);
        if (messageHeaders.size() > 0)
        {
            linkWith(messageHeaders.iterator().next(), andDisplay);
        }
        else if (discussionView instanceof PrivateDiscussionView)
        {
            ((PrivateDiscussionView) discussionView).setMessageType(MessageType.PRIVATE);
            //postCommentView.linkWith(MessageType.PRIVATE);
        }
        displayVisibilities();
    }

    public void linkWith(MessageHeaderDTO messageHeader, boolean andDisplay)
    {
        Timber.d("messageHeader %s", messageHeader);
        this.currentMessageHeader = messageHeader;

        DiscussionKey discussionKey = discussionKeyFactory.create(messageHeader);
        discussionView.display(discussionKey);

        //postCommentView.linkWith(discussionKey); // TODO remove

        nextDiscussionListKey = new PaginatedDiscussionListKey(
                DiscussionType.PRIVATE_MESSAGE,
                currentMessageHeader.id);
        //nextDiscussionListKey = new RangedDiscussionListKey(
        //        DiscussionType.PRIVATE_MESSAGE,
        //        currentMessageHeader.id,
        //        DEFAULT_MAX_COUNT,
        //        null,
        //        0);
        fetchDiscussionList();
    }

    public void linkWith(DiscussionKeyList discussionKeys, boolean andDisplay)
    {
        DiscussionDTOList additional = discussionCache.get(discussionKeys);
        loadedDiscussions.addAll(additional);
        // TODO identify the next nextDiscussionListKey
    }

    public void display()
    {
        displayCorrespondentImage();
        displayTitle();
        displayMessagingStatusContainer();
        displayMessagingStatusText();
        displayVisibilities();
    }

    private void displayCorrespondentImage()
    {
        if (correspondentImage != null)
        {
            RequestCreator picassoRequestCreator;
            if (correspondentProfile != null && correspondentProfile.picture != null && !correspondentProfile.picture.isEmpty())
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

    private void displayTitle()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        if (correspondentProfile != null)
        {
            String title = userBaseDTOUtil.getLongDisplayName(getSherlockActivity(), correspondentProfile);
            Timber.d("Display title " + title);
            actionBar.setTitle(title);
        }
        else
        {
            actionBar.setTitle(R.string.loading_loading);
        }
    }

    private void displayMessagingStatusContainer()
    {
        if (statusViewContainer != null)
        {
            // TODO better test
            statusViewContainer.setVisibility(
                    messageStatusDTO == null || messageStatusDTO.privateFreeRemainingCount == null
                            ? View.GONE : View.VISIBLE);
        }
    }

    private void displayMessagingStatusText()
    {
        if (statusViewText != null)
        {
            if (messageStatusDTO != null && messageStatusDTO.privateFreeRemainingCount != null)
            {
                statusViewText.setText(getResources().getString(R.string.private_message_limited_count, messageStatusDTO.privateFreeRemainingCount));
            }
        }
    }

    private void displayVisibilities()
    {
        //showOnly(currentMessageHeader == null ? emptyHint : messageListView);
    }

    private void showOnly(View view)
    {
        //if (messageListView != null)
        //{
        //    messageListView.setVisibility(view == messageListView ? View.VISIBLE : View.GONE);
        //}
        if (emptyHint != null)
        {
            emptyHint.setVisibility(view == emptyHint ? View.VISIBLE : View.GONE);
        }
    }

    protected void showPaidFollow()
    {
        cancelOthersAndShowProductDetailList(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS);
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    protected class PrivateMessageFragmentUserProfileListener implements DTOCache.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            Timber.e(error, "");
        }
    }

    protected class PrivateMessageFragmentMessageStatusListener implements DTOCache.Listener<UserBaseKey, MessageStatusDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, MessageStatusDTO value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            Timber.e(error, "");
        }
    }

    protected class PrivateMessageFragmentMessageListListener implements DTOCache.Listener<MessageListKey, MessageHeaderIdList>
    {
        @Override public void onDTOReceived(MessageListKey key, MessageHeaderIdList value, boolean fromCache)
        {
            setNextMessageListKey();
            linkWith(messageHeaderCache.getMessages(value), true);
        }

        @Override public void onErrorThrown(MessageListKey key, Throwable error)
        {
            Timber.e(error, "");
        }
    }

    protected class PrivateMessageFragmentDiscussionListListener implements DTOCache.Listener<DiscussionListKey, DiscussionKeyList>
    {
        @Override public void onDTOReceived(DiscussionListKey key, DiscussionKeyList value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(DiscussionListKey key, Throwable error)
        {
            Timber.e(error, "");
        }
    }

    protected class PrivateMessageFragmentOnMessageNotAllowedToSendListener
            implements PrivatePostCommentView.OnMessageNotAllowedToSendListener
    {
        @Override public void onMessageNotAllowedToSend()
        {
            showPaidFollow();
        }
    }

    protected class PrivateMessageFragmentUserFollowedListener extends BasePurchaseManagerUserFollowedListener
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
