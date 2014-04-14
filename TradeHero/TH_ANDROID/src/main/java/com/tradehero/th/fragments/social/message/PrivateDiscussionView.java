package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.fragments.discussion.DiscussionListAdapter;
import com.tradehero.th.fragments.discussion.DiscussionView;
import com.tradehero.th.fragments.discussion.PostCommentView;
import timber.log.Timber;

public class PrivateDiscussionView extends DiscussionView
{
    protected MessageType messageType;

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
        setLoaded();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (postCommentView != null)
        {
            postCommentView.setCommentPostedListener(new PrivateDiscussionViewCommentPostedListener());
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (postCommentView != null)
        {
            postCommentView.setCommentPostedListener(null);
        }
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

    protected class PrivateDiscussionViewCommentPostedListener implements PostCommentView.CommentPostedListener
    {
        @Override public void success(DiscussionDTO discussionDTO)
        {
            Timber.d("success %s", discussionDTO);
            addDiscussion(discussionDTO);
        }

        @Override public void failure(Exception exception)
        {
            // TODO better error
            THToast.show(R.string.error_unknown);
        }
    }
}
