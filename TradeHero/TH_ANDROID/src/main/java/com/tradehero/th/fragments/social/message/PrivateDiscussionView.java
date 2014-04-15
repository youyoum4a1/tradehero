package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.MessageStatusDTO;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.fragments.discussion.DiscussionListAdapter;
import com.tradehero.th.fragments.discussion.DiscussionView;

public class PrivateDiscussionView extends DiscussionView
{
    protected MessageType messageType;
    private MessageStatusDTO messageStatusDTO;
    private PrivatePostCommentView.OnMessageNotAllowedToSendListener messageNotAllowedToSendListener;

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
        return new PrivateDiscussionListAdapter(
                getContext(),
                LayoutInflater.from(getContext()),
                R.layout.private_message_bubble_mine,
                R.layout.private_message_bubble_other);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        setMessageNotAllowedListenerOnPostCommentView(new PrivateDiscussionViewOnMessageNotAllowedToSendListener());
        setMessageStatusDTO(messageStatusDTO);
        setLoaded();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        setMessageNotAllowedListenerOnPostCommentView(new PrivateDiscussionViewOnMessageNotAllowedToSendListener());
    }

    @Override protected void onDetachedFromWindow()
    {
        setMessageNotAllowedListenerOnPostCommentView(null);
        messageNotAllowedToSendListener = null;
        super.onDetachedFromWindow();
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

    public void setMessageStatusDTO(MessageStatusDTO messageStatusDTO)
    {
        this.messageStatusDTO = messageStatusDTO;
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
            ((PrivatePostCommentView) postCommentView).setMessageStatusDTO(messageStatusDTO);
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
}
