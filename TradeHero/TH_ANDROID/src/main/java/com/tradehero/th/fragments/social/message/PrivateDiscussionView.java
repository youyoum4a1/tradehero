package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.discussion.PrivateDiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.fragments.discussion.DiscussionListAdapter;
import com.tradehero.th.fragments.discussion.DiscussionView;

public class PrivateDiscussionView extends DiscussionView
{
    private DTOCache.GetOrFetchTask<DiscussionKey, AbstractDiscussionDTO> discussionFetchTask;
    protected DiscussionDTO initiatingDiscussion;

    protected MessageType messageType;
    private UserMessagingRelationshipDTO userMessagingRelationshipDTO;
    private PrivatePostCommentView.OnMessageNotAllowedToSendListener messageNotAllowedToSendListener;
    private boolean hasAddedHeader = false;

    //<editor-fold desc="Constructors">
    public PrivateDiscussionView(Context context)
    {
        super(context);
    }

    public PrivateDiscussionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PrivateDiscussionView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected DiscussionListAdapter createDiscussionListAdapter()
    {
        PrivateDiscussionListAdapter discussionListAdapter = new PrivateDiscussionListAdapter(
                getContext(),
                LayoutInflater.from(getContext()),
                R.layout.private_message_bubble_mine,
                R.layout.private_message_bubble_other);
        return discussionListAdapter;
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        setMessageNotAllowedListenerOnPostCommentView(new PrivateDiscussionViewOnMessageNotAllowedToSendListener());
        setUserMessagingRelationshipDTO(userMessagingRelationshipDTO);
        setLoaded();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        setMessageNotAllowedListenerOnPostCommentView(
                new PrivateDiscussionViewOnMessageNotAllowedToSendListener());
    }

    @Override protected void onDetachedFromWindow()
    {
        detachDiscussionFetchTask();
        setMessageNotAllowedListenerOnPostCommentView(null);
        super.onDetachedFromWindow();
    }

    private void detachDiscussionFetchTask()
    {
        if (discussionFetchTask != null)
        {
            discussionFetchTask.setListener(null);
        }
        discussionFetchTask = null;
    }

    @Override protected void setLoading()
    {
        super.setLoading();
        if (discussionStatus != null)
        {
            discussionStatus.setVisibility(View.VISIBLE);
        }
    }

    @Override protected void setLoaded()
    {
        super.setLoaded();
        if (discussionStatus != null)
        {
            discussionStatus.setVisibility(View.GONE);
        }
    }

    public void setMessageType(MessageType messageType)
    {
        this.messageType = messageType;

        if (postCommentView != null)
        {
            postCommentView.linkWith(messageType);
        }
    }

    @Override protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        super.linkWith(discussionKey, andDisplay);
        fetchDiscussion(discussionKey);
    }

    private void fetchDiscussion(DiscussionKey discussionKey)
    {
        detachDiscussionFetchTask();
        discussionFetchTask = discussionCache.getOrFetch(discussionKey, createDiscussionCacheListener());
        discussionFetchTask.execute();
    }

    @Override protected DiscussionListKey createListKey()
    {
        return discussionListKeyFactory.create(initiatingDiscussion);
    }

    @Override protected void prepareDiscussionListKey()
    {
        // Do nothing TODO something
    }

    protected DTOCache.Listener<DiscussionKey, AbstractDiscussionDTO> createDiscussionCacheListener()
    {
        return new PrivateDiscussionViewDiscussionCacheListener();
    }

    protected void linkWithInitiating(PrivateDiscussionDTO discussionDTO, boolean andDisplay)
    {
        this.initiatingDiscussion = discussionDTO;
        int topicId;
        if (currentUserId.toUserBaseKey().equals(discussionDTO.getSenderKey()))
        {
            topicId = ((PrivateDiscussionListAdapter) discussionListAdapter).mineResId;
        }
        else
        {
            topicId = ((PrivateDiscussionListAdapter) discussionListAdapter).otherResId;
        }
        setTopicLayout(topicId);
        inflateDiscussionTopic();
        initialFetchDiscussion();
        if (andDisplay)
        {
            displayTopicView();
        }
    }

    @Override protected void setTopicLayout(int topicLayout)
    {
        if (!hasAddedHeader)
        {
            super.setTopicLayout(topicLayout);
        }
    }

    @Override protected View inflateDiscussionTopic()
    {
        View inflated = null;
        if (!hasAddedHeader)
        {
            inflated = super.inflateDiscussionTopic();
            hasAddedHeader = inflated != null;
        }
        return inflated;
    }

    public void setUserMessagingRelationshipDTO(UserMessagingRelationshipDTO userMessagingRelationshipDTO)
    {
        this.userMessagingRelationshipDTO = userMessagingRelationshipDTO;
        setMessageStatusOnPostCommentView();
    }

    public void setMessageNotAllowedToSendListener(PrivatePostCommentView.OnMessageNotAllowedToSendListener messageNotAllowedToSendListener)
    {
        this.messageNotAllowedToSendListener = messageNotAllowedToSendListener;
    }

    private void setMessageStatusOnPostCommentView()
    {
        if (postCommentView != null)
        {
            ((PrivatePostCommentView) postCommentView).setUserMessagingRelationshipDTO(
                    userMessagingRelationshipDTO);
        }
    }

    private void setMessageNotAllowedListenerOnPostCommentView(PrivatePostCommentView.OnMessageNotAllowedToSendListener listener)
    {
        if (postCommentView != null)
        {
            ((PrivatePostCommentView) postCommentView).setMessageNotAllowedToSendListener(listener);
        }
    }

    private void notifyPreSubmissionInterceptListener()
    {
        PrivatePostCommentView.OnMessageNotAllowedToSendListener listener = messageNotAllowedToSendListener;
        if (listener != null)
        {
            listener.onMessageNotAllowedToSend();
        }
    }

    protected class PrivateDiscussionViewOnMessageNotAllowedToSendListener
            implements PrivatePostCommentView.OnMessageNotAllowedToSendListener
    {
        @Override public void onMessageNotAllowedToSend()
        {
            notifyPreSubmissionInterceptListener();
        }
    }

    protected class PrivateDiscussionViewDiscussionCacheListener implements DTOCache.Listener<DiscussionKey, AbstractDiscussionDTO>
    {
        @Override public void onDTOReceived(DiscussionKey key, AbstractDiscussionDTO value,
                boolean fromCache)
        {
            linkWithInitiating((PrivateDiscussionDTO) value, true);
        }

        @Override public void onErrorThrown(DiscussionKey key, Throwable error)
        {
            THToast.show(R.string.error_fetch_private_message_initiating_discussion);
        }
    }
}
