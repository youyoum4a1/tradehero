package com.tradehero.th.fragments.social.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTOList;
import com.tradehero.th.api.discussion.MessageHeaderIdList;
import com.tradehero.th.api.discussion.MessageStatusDTO;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.discussion.key.RangedDiscussionListKey;
import com.tradehero.th.api.discussion.key.RecipientTypedMessageListKey;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.discussion.PostCommentView;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCache;
import com.tradehero.th.persistence.discussion.MessageStatusCache;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import java.util.Collection;
import timber.log.Timber;

import javax.inject.Inject;

public class PrivateMessageFragment extends DashboardFragment
{
    public static final String CORRESPONDENT_USER_BASE_BUNDLE_KEY = PrivateMessageFragment.class.getName() + ".correspondentUserBaseKey";
    public static final int DEFAULT_MAX_COUNT = 10;

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
    private RangedDiscussionListKey nextDiscussionListKey;
    private DiscussionDTOList loadedDiscussions;

    @InjectView(R.id.private_message_empty) TextView emptyHint;
    @InjectView(R.id.message_list_view) ListView messageListView;
    PrivateMessageBubbleAdapter messageBubbleAdapter;
    @InjectView(R.id.discussion_comment_widget) PostCommentView postCommentView;
    //@InjectView(R.id.button_send) View buttonSend;
    @InjectView(R.id.post_comment_action_submit) TextView buttonSend;
    //@InjectView(R.id.typing_message_content) EditText messageToSend;
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
        nextLoadingMessageKey = new RecipientTypedMessageListKey(MessageListKey.FIRST_PAGE, 10, DiscussionType.PRIVATE_MESSAGE, correspondentId);
        loadedMessages = new MessageHeaderDTOList();
        loadedDiscussions = new DiscussionDTOList();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_private_message, container, false);
        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        postCommentView.setCommentPostedListener(new PrivateMessageFragmentCommentPostedListener());
        messageToSend.setHint(R.string.private_message_message_hint);
        buttonSend.setText(R.string.private_message_btn_send);
        swapAdapter();
        display();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.private_message_menu, menu);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);

        correspondentImage = (ImageView) menu.findItem(R.id.correspondent_picture);
        displayCorrespondentImage();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchCorrespondentProfile();
        fetchMessageStatus();
        fetchMessageList();
        //fetchDiscussionList();
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

        messageListView = null;
        messageBubbleAdapter = null;
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
        Timber.d("fetchMessageStatus");
        detachMessageStatusTask();
        messageStatusCacheTask = messageStatusCache.getOrFetch(correspondentId, messageStatusCacheListener);
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
        }
    }

    public void linkWith(MessageStatusDTO messageStatusDTO, boolean andDisplay)
    {
        this.messageStatusDTO = messageStatusDTO;
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
        else
        {
            postCommentView.linkWith(MessageType.PRIVATE);
        }
        displayVisibilities();
    }

    public void linkWith(MessageHeaderDTO messageHeader, boolean andDisplay)
    {
        Timber.d("messageHeader %s", messageHeader);
        this.currentMessageHeader = messageHeader;
        // TODO postCommentView.linkWith discussionKey
        nextDiscussionListKey = new RangedDiscussionListKey(
                DiscussionType.PRIVATE_MESSAGE,
                currentMessageHeader.id,
                DEFAULT_MAX_COUNT,
                null,
                0);
        fetchDiscussionList();
    }

    public void linkWith(DiscussionKeyList discussionKeys, boolean andDisplay)
    {
        DiscussionDTOList additional = discussionCache.get(discussionKeys);
        loadedDiscussions.addAll(additional);
        swapAdapter();
        // TODO identify the next nextDiscussionListKey
    }

    public void swapAdapter()
    {
        messageBubbleAdapter = new PrivateMessageBubbleAdapter(getSherlockActivity(), loadedDiscussions);
        messageListView.setAdapter(messageBubbleAdapter);
    }

    public void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        loadedDiscussions.add(discussionDTO);
        swapAdapter();
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
            actionBar.setTitle(userBaseDTOUtil.getLongDisplayName(getSherlockActivity(), correspondentProfile));
        }
    }

    private void displayMessagingStatusContainer()
    {
        if (statusViewContainer != null)
        {
            // TODO better test
            statusViewContainer.setVisibility(messageStatusDTO == null || messageStatusDTO.privateFreeRemainingCount == null ? View.GONE : View.VISIBLE);
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
        showOnly(currentMessageHeader == null ? emptyHint : messageListView);
    }

    private void showOnly(View view)
    {
        if (messageListView != null)
        {
            messageListView.setVisibility(view == messageListView ? View.VISIBLE : View.GONE);
        }
        if (emptyHint != null)
        {
            emptyHint.setVisibility(view == emptyHint ? View.VISIBLE : View.GONE);
        }
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

    protected class PrivateMessageFragmentCommentPostedListener implements PostCommentView.CommentPostedListener
    {
        @Override public void success(DiscussionDTO discussionDTO)
        {
            handleCommentPosted(discussionDTO);
        }

        @Override public void failure()
        {
            // Do something?
        }
    }
}
