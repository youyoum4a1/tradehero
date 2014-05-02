package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTOFactory;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.discussion.PrivateDiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.MessageDiscussionListKey;
import com.tradehero.th.api.discussion.key.MessageDiscussionListKeyFactory;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.discussion.DiscussionSetAdapter;
import com.tradehero.th.fragments.discussion.DiscussionView;
import com.tradehero.th.fragments.discussion.PostCommentView;
import com.tradehero.th.fragments.discussion.PrivateDiscussionSetAdapter;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import javax.inject.Inject;

public class PrivateDiscussionView extends DiscussionView
{
    @Inject protected MessageHeaderCache messageHeaderCache;
    @Inject protected MessageHeaderDTOFactory messageHeaderDTOFactory;
    @Inject protected MessageDiscussionListKeyFactory messageDiscussionListKeyFactory;
    private MessageHeaderDTO messageHeaderDTO;

    private DTOCache.GetOrFetchTask<DiscussionKey, AbstractDiscussionDTO> discussionFetchTask;
    protected DiscussionDTO initiatingDiscussion;

    protected MessageType messageType;
    private UserBaseKey recipient;
    private boolean hasAddedHeader = false;
    private DTOCache.Listener<MessageHeaderId, MessageHeaderDTO> messageHeaderFetchListener;
    private DTOCache.GetOrFetchTask<MessageHeaderId, MessageHeaderDTO> messageHeaderFetchTask;

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

    @Override protected DiscussionSetAdapter createDiscussionListAdapter()
    {
        return new PrivateDiscussionViewDiscussionSetAdapter();
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        setRecipientOnPostCommentView();
        setLoaded();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachDiscussionFetchTask();

        detachMessageHeaderFetchTask();
        messageHeaderFetchListener = null;

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
        MessageHeaderId messageHeaderId = new MessageHeaderId(discussionKey.id);
        this.messageHeaderDTO = messageHeaderCache.get(messageHeaderId);
        super.linkWith(discussionKey, andDisplay);

        if (messageHeaderDTO != null)
        {
            fetchDiscussion(discussionKey);
        }
        else
        {
            detachMessageHeaderFetchTask();
            messageHeaderFetchListener = new MessageHeaderFetchListener();
            messageHeaderFetchTask = messageHeaderCache.getOrFetch(messageHeaderId, false, messageHeaderFetchListener);
            messageHeaderFetchTask.execute();
        }
    }

    private void detachMessageHeaderFetchTask()
    {
        if (messageHeaderFetchTask != null)
        {
            messageHeaderFetchTask.setListener(null);
        }
        messageHeaderFetchTask = null;
    }

    private void fetchDiscussion(DiscussionKey discussionKey)
    {
        detachDiscussionFetchTask();
        discussionFetchTask = discussionCache.getOrFetch(discussionKey, createDiscussionCacheListener());
        discussionFetchTask.execute();
    }

    @Override protected DiscussionListKey createStartingDiscussionListKey()
    {
        return discussionListKeyFactory.create(messageHeaderDTO);
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
            topicId = ((PrivateDiscussionSetAdapter) discussionListAdapter).mineResId;
        }
        else
        {
            topicId = ((PrivateDiscussionSetAdapter) discussionListAdapter).otherResId;
        }
        setTopicLayout(topicId);
        inflateDiscussionTopic();
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

    public void setRecipient(UserBaseKey recipient)
    {
        this.recipient = recipient;
        setRecipientOnPostCommentView();
    }

    private void setRecipientOnPostCommentView()
    {
        if (postCommentView != null)
        {
            ((PrivatePostCommentView) postCommentView).setRecipient(recipient);
        }
    }

    protected void putMessageHeaderStub(DiscussionDTO from)
    {
        messageHeaderCache.put(new MessageHeaderId(from.id),
                createStub(from));
    }

    protected MessageHeaderDTO createStub(DiscussionDTO from)
    {
        MessageHeaderDTO stub = messageHeaderDTOFactory.create(from);
        stub.recipientUserId = recipient.key;
        return stub;
    }

    @Override protected DiscussionListKey getNextDiscussionListKey(DiscussionListKey currentNext, DiscussionKeyList latestDiscussionKeys)
    {
        DiscussionListKey next = messageDiscussionListKeyFactory.next((MessageDiscussionListKey) currentNext, latestDiscussionKeys);
        if (next != null && next.equals(currentNext))
        {
            // This situation where next is equal to currentNext may happen
            // when the server is still returning the same values
            next = null;
        }
        return next;
    }

    @Override protected DiscussionListKey getPrevDiscussionListKey(DiscussionListKey currentPrev, DiscussionKeyList latestDiscussionKeys)
    {
        DiscussionListKey prev = messageDiscussionListKeyFactory.prev((MessageDiscussionListKey) currentPrev, latestDiscussionKeys);
        if (prev != null && prev.equals(currentPrev))
        {
            // This situation where next is equal to currentNext may happen
            // when the server is still returning the same values
            prev = null;
        }
        return prev;
    }

    @Override protected PostCommentView.CommentPostedListener createCommentPostedListener()
    {
        return new PrivateDiscussionViewCommentPostedListener();
    }

    protected class PrivateDiscussionViewDiscussionCacheListener implements DTOCache.Listener<DiscussionKey, AbstractDiscussionDTO>
    {
        @Override public void onDTOReceived(DiscussionKey key, AbstractDiscussionDTO value, boolean fromCache)
        {
            linkWithInitiating((PrivateDiscussionDTO) value, true);
        }

        @Override public void onErrorThrown(DiscussionKey key, Throwable error)
        {
            THToast.show(R.string.error_fetch_private_message_initiating_discussion);
        }
    }

    protected class PrivateDiscussionViewCommentPostedListener extends DiscussionViewCommentPostedListener
    {
        @Override public void success(DiscussionDTO discussionDTO)
        {
            putMessageHeaderStub(discussionDTO);
            super.success(discussionDTO);
        }
    }

    public class PrivateDiscussionViewDiscussionSetAdapter extends PrivateDiscussionSetAdapter
    {
        protected PrivateDiscussionViewDiscussionSetAdapter()
        {
            super(getContext(),
                    LayoutInflater.from(getContext()),
                    R.layout.private_message_bubble_mine,
                    R.layout.private_message_bubble_other);
        }

        @Override public View getView(int position, View convertView, ViewGroup parent)
        {
            if (position == 0)
            {
                // TODO do something similar better
                //scrollListener.raiseStartFlag();
            }
            return super.getView(position, convertView, parent);
        }
    }


    private class MessageHeaderFetchListener implements DTOCache.Listener<MessageHeaderId,MessageHeaderDTO>
    {
        @Override public void onDTOReceived(MessageHeaderId key, MessageHeaderDTO value, boolean fromCache)
        {
            linkWith(discussionKey, true);
        }

        @Override public void onErrorThrown(MessageHeaderId key, Throwable error)
        {
            THToast.show(new THException(error));
        }
    }
}
