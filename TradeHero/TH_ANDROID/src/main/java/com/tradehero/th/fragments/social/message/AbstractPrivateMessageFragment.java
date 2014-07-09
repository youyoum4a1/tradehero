package com.tradehero.th.fragments.social.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
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
import com.tradehero.th.fragments.discussion.AbstractDiscussionFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    @Nullable private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected UserBaseKey correspondentId;
    protected UserProfileDTO correspondentProfile;

    @InjectView(R.id.private_message_empty) protected TextView emptyHint;
    @InjectView(R.id.post_comment_action_submit) protected TextView buttonSend;
    @InjectView(R.id.post_comment_text) protected EditText messageToSend;

    private DTOCacheNew.Listener<MessageHeaderId, MessageHeaderDTO> messageHeaderFetchListener;
    private MessageHeaderId messageHeaderId;

    public static void putCorrespondentUserBaseKey(@NotNull Bundle args, @NotNull UserBaseKey correspondentBaseKey)
    {
        args.putBundle(CORRESPONDENT_USER_BASE_BUNDLE_KEY, correspondentBaseKey.getArgs());
    }

    @NotNull private static UserBaseKey collectCorrespondentId(@NotNull Bundle args)
    {
        return new UserBaseKey(args.getBundle(CORRESPONDENT_USER_BASE_BUNDLE_KEY));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        messageHeaderFetchListener = createMessageHeaderCacheListener();
        correspondentId = collectCorrespondentId(getArguments());
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
        if (discussionView != null)
        {
            ((PrivateDiscussionView) discussionView).setMessageType(MessageType.PRIVATE);
            ((PrivateDiscussionView) discussionView).setRecipient(correspondentId);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.private_message_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.private_message_refresh_btn:
                refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchCorrespondentProfile();
    }

    @Override public void onDetach()
    {
        setActionBarSubtitle(null);
        super.onDetach();
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
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
        messageHeaderFetchListener = null;
        super.onDestroy();
    }

    @Override protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        super.linkWith(discussionKey, andDisplay);

        linkWith(new MessageHeaderUserId(discussionKey.id, correspondentId), true);
    }

    private void linkWith(MessageHeaderId messageHeaderId, boolean andDisplay)
    {
        this.messageHeaderId = messageHeaderId;
        detachMessageHeaderFetchTask();
        messageHeaderCache.register(messageHeaderId, messageHeaderFetchListener);
        messageHeaderCache.getOrFetchAsync(messageHeaderId, false);
    }

    private void detachMessageHeaderFetchTask()
    {
        messageHeaderCache.unregister(messageHeaderFetchListener);
    }

    protected void refresh()
    {
        if (getDiscussionKey() != null && discussionView != null)
        {
            discussionView.refresh();

            if (messageHeaderId != null)
            {
                MessageHeaderDTO messageHeaderDTO = messageHeaderCache.get(messageHeaderId);
                if (messageHeaderDTO != null)
                {
                    reportMessageRead(messageHeaderDTO);
                }
            }
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
            getSherlockActivity().invalidateOptionsMenu();
        }
    }

    //TODO set actionBar with MessageHeaderDTO by alex


    @Override protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
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

    private DTOCacheNew.Listener<MessageHeaderId, MessageHeaderDTO> createMessageHeaderCacheListener()
    {
        return new MessageHeaderFetchListener();
    }

    private class MessageHeaderFetchListener
            implements DTOCacheNew.Listener<MessageHeaderId, MessageHeaderDTO>
    {
        @Override
        public void onDTOReceived(@NotNull MessageHeaderId key, @NotNull MessageHeaderDTO value)
        {
            Timber.d("MessageHeaderDTO=%s", value);
            setActionBarTitle(value.title);
            setActionBarSubtitle(value.subTitle);
            if (value.unread)
            {
                reportMessageRead(value);
            }
        }

        @Override public void onErrorThrown(@NotNull MessageHeaderId key, @NotNull Throwable error)
        {
            THToast.show(new THException(error));
        }
    }
}
