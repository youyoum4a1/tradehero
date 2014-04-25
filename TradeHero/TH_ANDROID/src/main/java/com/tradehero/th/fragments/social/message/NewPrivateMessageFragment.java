package com.tradehero.th.fragments.social.message;

import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.user.FollowUserAssistant;
import com.tradehero.th.persistence.message.MessageThreadHeaderCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import javax.inject.Inject;
import retrofit.RetrofitError;
import timber.log.Timber;

public class NewPrivateMessageFragment extends AbstractPrivateMessageFragment
{
    protected boolean isFresh = true;

    @Inject protected UserMessagingRelationshipCache userMessagingRelationshipCache;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserMessagingRelationshipDTO>
            messagingRelationshipCacheTask;
    protected UserMessagingRelationshipDTO userMessagingRelationshipDTO;

    @Inject protected MessageThreadHeaderCache messageThreadHeaderCache;
    protected DTOCache.GetOrFetchTask<UserBaseKey, MessageHeaderDTO> messageThreadHeaderFetchTask;

    @InjectView(R.id.private_message_status_container) protected View statusViewContainer;
    @InjectView(R.id.private_message_status_text) protected TextView statusViewText;

    protected DTOCache.Listener<UserBaseKey, UserMessagingRelationshipDTO> createMessageStatusCacheListener()
    {
        return new AbstractPrivateMessageFragmentMessageStatusListener();
    }

    @Override protected FollowUserAssistant.OnUserFollowedListener createUserFollowedListener()
    {
        return new NewPrivateMessageFragmentUserFollowedListener();
    }

    @Override protected void initViews(View view)
    {
        super.initViews(view);
        ((PrivateDiscussionView) discussionView).setUserMessagingRelationshipDTO(
                userMessagingRelationshipDTO);
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchMessageStatus();
        fetchMessageThreadHeader();
    }

    @Override public void onDestroyView()
    {
        detachMessageStatusTask();
        detachMessageThreadHeaderFetchTask();
        super.onDestroyView();
    }

    private void detachMessageStatusTask()
    {
        if (messagingRelationshipCacheTask != null)
        {
            messagingRelationshipCacheTask.setListener(null);
        }
        messagingRelationshipCacheTask = null;
    }

    protected void detachMessageThreadHeaderFetchTask()
    {
        if (messageThreadHeaderFetchTask != null)
        {
            messageThreadHeaderFetchTask.setListener(null);
        }
        messageThreadHeaderFetchTask = null;
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

    protected void fetchMessageThreadHeader()
    {
        detachMessageThreadHeaderFetchTask();
        messageThreadHeaderFetchTask = messageThreadHeaderCache.getOrFetch(correspondentId, createMessageThreadHeaderCacheListener());
        messageThreadHeaderFetchTask.execute();
    }

    public void linkWith(UserMessagingRelationshipDTO messageStatusDTO, boolean andDisplay)
    {
        this.userMessagingRelationshipDTO = messageStatusDTO;
        setUserMessagingRelationshipOnDiscussionView();
        //TODO
        if (andDisplay)
        {
            displayMessagingStatusContainer();
            displayMessagingStatusText();
        }
    }

    protected void setUserMessagingRelationshipOnDiscussionView()
    {
        Timber.d("setUserMessagingRelationshipOnDiscussionView %s %s",
                discussionView == null ? "null" : discussionView,
                userMessagingRelationshipDTO);
        if (discussionView != null)
        {
        }
        ((PrivateDiscussionView) discussionView).setUserMessagingRelationshipDTO(
                userMessagingRelationshipDTO);
    }

    @Override public void display()
    {
        super.display();
        displayMessagingStatusContainer();
        displayMessagingStatusText();
    }

    protected void displayMessagingStatusContainer()
    {
        if (statusViewContainer != null)
        {
            //TODO hardcode for design not sure alex
            statusViewContainer.setVisibility(View.GONE);
            //statusViewContainer.setVisibility(shouldShowStatusContainer() ? View.VISIBLE : View.GONE);
        }
    }

    public boolean shouldShowStatusContainer()
    {
        return isFresh && userMessagingRelationshipDTO != null &&
                !userMessagingRelationshipDTO.isUnlimited();
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

    @Override protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        super.handleCommentPosted(discussionDTO);
        isFresh = false;
        userMessagingRelationshipCache.invalidate(correspondentId);
        displayMessagingStatusContainer();
        if (getDiscussionKey() == null)
        {
            // We do this in order to ensure the next message is not a new one.
            linkWith(discussionDTO.getDiscussionKey(), true);
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

    protected class NewPrivateMessageFragmentUserFollowedListener
            extends AbstractPrivateMessageFragmentUserFollowedListener
    {
        @Override public void onUserFollowSuccess(UserBaseKey userFollowed,
                UserProfileDTO currentUserProfileDTO)
        {
            super.onUserFollowSuccess(userFollowed, currentUserProfileDTO);
            fetchMessageStatus(true);
        }
    }

    protected DTOCache.Listener<UserBaseKey, MessageHeaderDTO> createMessageThreadHeaderCacheListener()
    {
        return new NewPrivateMessageFragmentThreadHeaderCacheListener();
    }

    protected class NewPrivateMessageFragmentThreadHeaderCacheListener implements DTOCache.Listener<UserBaseKey, MessageHeaderDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, MessageHeaderDTO value,
                boolean fromCache)
        {
            if (getDiscussionKey() == null)
            {
                linkWith(discussionKeyFactory.create(value), true);
            }
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            if (!(error instanceof RetrofitError) ||
                    ((RetrofitError) error).getResponse().getStatus() != 404)
            {
                THToast.show(R.string.error_fetch_message_thread_header);
                Timber.e(error, "Error while getting message thread");
            }
            else
            {
                // There is just no existing thread
            }
        }
    }
}
