package com.tradehero.th.fragments.social.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.discussion.key.MessageHeaderUserId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.fragments.discussion.AbstractDiscussionFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

abstract public class AbstractPrivateMessageFragment extends AbstractDiscussionFragment
{
    private static final String CORRESPONDENT_USER_BASE_BUNDLE_KEY =
            AbstractPrivateMessageFragment.class.getName() + ".correspondentUserBaseKey";

    @Inject protected MessageHeaderCache messageHeaderCache;
    @Inject protected MessageHeaderListCache messageHeaderListCache;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected UserBaseDTOUtil userBaseDTOUtil;
    @Inject protected Picasso picasso;
    @Inject @ForUserPhoto protected Transformation userPhotoTransformation;
    @Inject protected UserProfileCache userProfileCache;
    @Inject Lazy<MessageServiceWrapper> messageServiceWrapper;

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected UserBaseKey correspondentId;
    protected UserProfileDTO correspondentProfile;

    protected ImageView correspondentImage;
    @InjectView(R.id.private_message_empty) protected TextView emptyHint;
    @InjectView(R.id.post_comment_action_submit) protected TextView buttonSend;
    @InjectView(R.id.post_comment_text) protected EditText messageToSend;

    private DTOCache.GetOrFetchTask<MessageHeaderId, MessageHeaderDTO> messageHeaderFetchTask;

    public static void putCorrespondentUserBaseKey(Bundle args, UserBaseKey correspondentBaseKey)
    {
        args.putBundle(CORRESPONDENT_USER_BASE_BUNDLE_KEY, correspondentBaseKey.getArgs());
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        collectCorrespondentId();
    }

    private UserBaseKey collectCorrespondentId()
    {
        Bundle args = getArguments();
        if (args != null && args.containsKey(CORRESPONDENT_USER_BASE_BUNDLE_KEY))
        {
            return new UserBaseKey(args.getBundle(CORRESPONDENT_USER_BASE_BUNDLE_KEY));
        }
        return null;
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new AbstractPrivateMessageFragmentUserProfileListener();
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
        super.initViews(view);

        messageToSend.setHint(R.string.private_message_message_hint);
        buttonSend.setText(R.string.private_message_btn_send);
        display();
        correspondentId = collectCorrespondentId();
        if (discussionView != null)
        {
            ((PrivateDiscussionView) discussionView).setMessageType(MessageType.PRIVATE);
            ((PrivateDiscussionView) discussionView).setRecipient(correspondentId);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.private_message_menu, menu);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_SHOW_HOME);
        //actionBar.setSubtitle(R.string.private_message_subtitle);

        correspondentImage = (ImageView) menu.findItem(R.id.correspondent_picture);
        //displayTitle();
        displayCorrespondentImage();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.private_message_refresh_btn:
                refresh();
                return true;
            case android.R.id.home:
                getDashboardNavigator().goToTab(DashboardTabType.UPDATE_CENTER);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onResume()
    {
        super.onResume();
        //TODO need this? temp remove by alex
        //fetchCorrespondentProfile();
    }

    @Override public void onDestroyOptionsMenu()
    {
        SherlockFragmentActivity activity = getSherlockActivity();
        if (activity != null)
        {
            ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setSubtitle(null);
        }
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        detachMessageHeaderFetchTask();
        detachUserProfileTask();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    private void detachUserProfileTask()
    {
        if (userProfileCacheListener != null)
        {
            userProfileCache.unregister(userProfileCacheListener);
        }
        userProfileCacheListener = null;
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        super.linkWith(discussionKey, andDisplay);

        linkWith(new MessageHeaderUserId(discussionKey.id, collectCorrespondentId()), true);
    }

    private void linkWith(MessageHeaderId messageHeaderId, boolean andDisplay)
    {
        detachMessageHeaderFetchTask();
        messageHeaderFetchTask = messageHeaderCache.getOrFetch(messageHeaderId, false,
                createMessageHeaderCacheListener());
        messageHeaderFetchTask.execute();
    }

    private void detachMessageHeaderFetchTask()
    {
        if (messageHeaderFetchTask != null)
        {
            messageHeaderFetchTask.setListener(null);
        }
        messageHeaderFetchTask = null;
    }

    private DTOCache.Listener<MessageHeaderId, MessageHeaderDTO> createMessageHeaderCacheListener()
    {
        return new MessageHeaderFetchListener();
    }

    protected void refresh()
    {
        if (getDiscussionKey() != null && discussionView != null)
        {
            discussionView.refresh();
        }
    }

    private void fetchCorrespondentProfile()
    {
        Timber.d("fetchCorrespondentProfile");
        detachUserProfileTask();
        userProfileCacheListener = createUserProfileCacheListener();
        userProfileCache.register(correspondentId, userProfileCacheListener);
        userProfileCache.getOrFetchAsync(correspondentId);
    }

    public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        Timber.d("userProfile %s", userProfileDTO);
        correspondentProfile = userProfileDTO;
        if (andDisplay)
        {
            displayCorrespondentImage();
            //displayTitle();
            getSherlockActivity().invalidateOptionsMenu();
        }
    }

    public void display()
    {
        displayCorrespondentImage();
        //displayTitle();
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

    //TODO set actionBar with MessageHeaderDTO by alex
    //protected void displayTitle()
    //{
    //    ActionBar actionBar = getSherlockActivity().getSupportActionBar();
    //    if (correspondentProfile != null)
    //    {
    //        String title = userBaseDTOUtil.getLongDisplayName(getSherlockActivity(), correspondentProfile);
    //        Timber.d("Display title " + title);
    //        actionBar.setTitle(title);
    //    }
    //    else
    //    {
    //        actionBar.setTitle(R.string.loading_loading);
    //    }
    //}

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    @Override protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        super.handleCommentPosted(discussionDTO);
        messageHeaderListCache.invalidateWithRecipient(correspondentId);
    }

    protected class AbstractPrivateMessageFragmentUserProfileListener
            implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override
        public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            Timber.e(error, "");
        }
    }

    private void updateMessageCacheReadStatus(int messageId)
    {
        // mark it as read in the cache
        MessageHeaderId messageHeaderId = new MessageHeaderId(messageId);
        MessageHeaderDTO messageHeaderDTO = messageHeaderCache.get(messageHeaderId);
        if (messageHeaderDTO != null && messageHeaderDTO.unread)
        {
            messageHeaderDTO.unread = false;
            messageHeaderCache.put(messageHeaderId, messageHeaderDTO);
        }
    }

    private void reportMessageRead(MessageHeaderDTO messageHeaderDTO)
    {
        updateMessageCacheReadStatus(messageHeaderDTO.id);
        messageServiceWrapper.get().readMessage(
                messageHeaderDTO.id,
                messageHeaderDTO.senderUserId,
                messageHeaderDTO.recipientUserId,
                messageHeaderDTO.getDTOKey(),
                currentUserId.toUserBaseKey(),
                createMessageAsReadCallback(messageHeaderDTO.id));
    }

    private Callback<Response> createMessageAsReadCallback(int pushId)
    {
        return new MessageMarkAsReadCallback(pushId);
    }

    private class MessageMarkAsReadCallback implements Callback<Response>
    {
        private final int messageId;

        public MessageMarkAsReadCallback(int messageId)
        {
            this.messageId = messageId;
        }

        @Override public void success(Response response, Response response2)
        {
            if (response.getStatus() == 200)
            {
                updateMessageCacheReadStatus(messageId);
            }
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            Timber.d("Report failure for Message: %d", messageId);
        }
    }

    private class MessageHeaderFetchListener
            implements DTOCache.Listener<MessageHeaderId, MessageHeaderDTO>
    {
        @Override
        public void onDTOReceived(MessageHeaderId key, MessageHeaderDTO value, boolean fromCache)
        {
            Timber.d("MessageHeaderDTO=%s", value);
            ActionBar actionBar = getSherlockActivity().getSupportActionBar();
            if (actionBar != null)
            {
                actionBar.setTitle(value.title);
                actionBar.setSubtitle(value.subTitle);
            }
            correspondentId = new UserBaseKey(value.recipientUserId);
            fetchCorrespondentProfile();
            if (value != null && value.unread)
            {
                reportMessageRead(value);
            }
        }

        @Override public void onErrorThrown(MessageHeaderId key, Throwable error)
        {
            THToast.show(new THException(error));
        }
    }
}
