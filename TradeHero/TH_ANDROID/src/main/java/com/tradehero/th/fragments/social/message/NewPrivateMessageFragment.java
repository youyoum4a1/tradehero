package com.tradehero.th.fragments.social.message;

import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.user.FollowUserAssistant;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import javax.inject.Inject;
import timber.log.Timber;

public class NewPrivateMessageFragment extends AbstractPrivateMessageFragment
{
    protected boolean isFresh = true;

    @Inject protected UserMessagingRelationshipCache userMessagingRelationshipCache;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserMessagingRelationshipDTO>
            messagingRelationshipCacheTask;
    protected UserMessagingRelationshipDTO userMessagingRelationshipDTO;

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
    }

    @Override public void onDestroyView()
    {
        detachMessageStatusTask();
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
            statusViewContainer.setVisibility(shouldShowStatusContainer() ? View.VISIBLE : View.GONE);
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
}
