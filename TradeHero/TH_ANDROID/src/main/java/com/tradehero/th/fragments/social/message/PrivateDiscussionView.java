package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTOFactory;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.MessageDiscussionListKey;
import com.tradehero.th.api.discussion.key.MessageDiscussionListKeyFactory;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.discussion.key.MessageHeaderUserId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.DiscussionSetAdapter;
import com.tradehero.th.fragments.discussion.DiscussionView;
import com.tradehero.th.fragments.discussion.PostCommentView;
import com.tradehero.th.fragments.discussion.PrivateDiscussionSetAdapter;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.RetrofitError;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class PrivateDiscussionView extends DiscussionView
{
    @Inject CurrentUserId currentUserId;
    @Inject DiscussionCacheRx discussionCache;
    @Inject protected MessageHeaderCache messageHeaderCache;
    @Inject protected MessageHeaderDTOFactory messageHeaderDTOFactory;
    @Inject protected MessageDiscussionListKeyFactory messageDiscussionListKeyFactory;
    private MessageHeaderDTO messageHeaderDTO;

    private Subscription discussionFetchSubscription;
    protected DiscussionDTO initiatingDiscussion;

    protected MessageType messageType;
    private UserBaseKey recipient;
    private boolean hasAddedHeader = false;
    private DTOCacheNew.Listener<MessageHeaderId, MessageHeaderDTO> messageHeaderFetchListener;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public PrivateDiscussionView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public PrivateDiscussionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
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
        messageHeaderFetchListener = createMessageHeaderListener();
        setLoaded();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (messageHeaderFetchListener == null)
        {
            messageHeaderFetchListener = createMessageHeaderListener();
        }
        setRecipientOnPostCommentView();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachDiscussionFetchTask();
        detachMessageHeaderFetchTask();
        messageHeaderFetchListener = null;
        discussionFetchSubscription = null;
        super.onDetachedFromWindow();
    }

    private void detachDiscussionFetchTask()
    {
        Subscription copy = discussionFetchSubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        discussionFetchSubscription = null;
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
        MessageHeaderId messageHeaderId = new MessageHeaderUserId(discussionKey.id, recipient);
        this.messageHeaderDTO = messageHeaderCache.get(messageHeaderId);
        super.linkWith(discussionKey, andDisplay);

        if (messageHeaderDTO != null)
        {
            fetchDiscussion(discussionKey);
        }
        else
        {
            detachMessageHeaderFetchTask();
            messageHeaderCache.register(messageHeaderId, messageHeaderFetchListener);
            messageHeaderCache.getOrFetchAsync(messageHeaderId, false);
        }
    }

    private void detachMessageHeaderFetchTask()
    {
        messageHeaderCache.unregister(messageHeaderFetchListener);
    }

    private void fetchDiscussion(DiscussionKey discussionKey)
    {
        detachDiscussionFetchTask();
        discussionFetchSubscription = discussionCache.get(discussionKey)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createDiscussionCacheObserver());
    }

    @Override protected DiscussionListKey createStartingDiscussionListKey()
    {
        return discussionListKeyFactory.create(messageHeaderDTO);
    }

    protected Observer<Pair<DiscussionKey, AbstractDiscussionCompactDTO>> createDiscussionCacheObserver()
    {
        return new PrivateDiscussionViewDiscussionCacheObserver();
    }

    protected void linkWithInitiating(DiscussionDTO discussionDTO, boolean andDisplay)
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

    public void setRecipient(@NotNull UserBaseKey recipient)
    {
        this.recipient = recipient;
        setRecipientOnPostCommentView();
    }

    private void setRecipientOnPostCommentView()
    {
        if (postCommentView != null && recipient != null)
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

    protected class PrivateDiscussionViewDiscussionCacheObserver implements Observer<Pair<DiscussionKey, AbstractDiscussionCompactDTO>>
    {
        @Override public void onNext(Pair<DiscussionKey, AbstractDiscussionCompactDTO> pair)
        {
            // Check with instanceof to avoid ClassCastException.
            if (pair.second instanceof DiscussionDTO)
            {
                linkWithInitiating((DiscussionDTO) pair.second, true);
            }
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
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
                    discussionCache,
                    currentUserId,
                    R.layout.private_message_bubble_mine,
                    R.layout.private_message_bubble_other);
        }

        @Override public AbstractDiscussionCompactItemViewLinear<DiscussionKey> getView(int position, View convertView, ViewGroup parent)
        {
            if (position == 0)
            {
                // TODO do something similar better
                //scrollListener.raiseStartFlag();
            }
            return super.getView(position, convertView, parent);
        }
    }

    protected DTOCacheNew.Listener<MessageHeaderId, MessageHeaderDTO> createMessageHeaderListener()
    {
        return new MessageHeaderFetchListener();
    }

    private class MessageHeaderFetchListener implements DTOCacheNew.Listener<MessageHeaderId, MessageHeaderDTO>
    {
        @Override public void onDTOReceived(@NotNull MessageHeaderId key, @NotNull MessageHeaderDTO value)
        {
            setRecipient(new UserBaseKey(value.recipientUserId));
            linkWith(discussionKey, true);
            refresh();
        }

        @Override public void onErrorThrown(@NotNull MessageHeaderId key, @NotNull Throwable error)
        {
            if (error instanceof RetrofitError)
            {
                THToast.show(new THException(error));
            }
        }
    }
}
