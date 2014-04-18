package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.form.DiscussionFormDTOFactory;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTOFactory;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PostCommentView extends RelativeLayout
{
    @InjectView(R.id.post_comment_action_submit) TextView commentSubmit;
    @InjectView(R.id.post_comment_action_processing) View commentActionProcessing;
    @InjectView(R.id.post_comment_action_wrapper) BetterViewAnimator commentActionWrapper;
    @InjectView(R.id.post_comment_text) EditText commentText;

    private MiddleCallback<DiscussionDTO> postCommentMiddleCallback;

    @Inject MessageServiceWrapper messageServiceWrapper;
    private MessageType messageType = null;
    @Inject MessageCreateFormDTOFactory messageCreateFormDTOFactory;
    @Inject CurrentUserId currentUserId;

    @Inject DiscussionServiceWrapper discussionServiceWrapper;
    private DiscussionKey discussionKey = null;
    @Inject DiscussionFormDTOFactory discussionFormDTOFactory;
    private CommentPostedListener commentPostedListener;

    //<editor-fold desc="Constructors">
    public PostCommentView(Context context)
    {
        super(context);
    }

    public PostCommentView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PostCommentView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        DaggerUtils.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachSubmitCommentMiddleCallback();

        resetView();

        commentPostedListener = null;

        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @OnClick(R.id.post_comment_action_submit)
    protected void postComment()
    {
        detachSubmitCommentMiddleCallback();

        if (!validate())
        {
            THToast.show(R.string.error_empty_comment);
        }
        else if (discussionKey != null)
        {
            DiscussionFormDTO discussionFormDTO = buildCommentFormDTO();
            setPosting();
            postCommentMiddleCallback = discussionServiceWrapper.createDiscussion(discussionFormDTO, new CommentSubmitCallback());
        }
        else if (messageType != null)
        {
            MessageCreateFormDTO messageCreateFormDTO = buildMessageCreateFormDTO();
            setPosting();
            postCommentMiddleCallback = messageServiceWrapper.createMessage(messageCreateFormDTO, new CommentSubmitCallback());
        }
        else
        {
            THToast.show(R.string.error_not_enough_information);
        }
    }

    protected boolean validate()
    {
        String comment = commentText.getText().toString();
        if (comment == null || comment.trim().isEmpty())
        {
            return false;
        }
        return true;
    }

    protected MessageCreateFormDTO buildMessageCreateFormDTO()
    {
        MessageCreateFormDTO messageCreateFormDTO = messageCreateFormDTOFactory.createEmpty(messageType);
        messageCreateFormDTO.message = commentText.getText().toString();
        messageCreateFormDTO.senderUserId = currentUserId.toUserBaseKey().key;
        return messageCreateFormDTO;
    }

    protected DiscussionFormDTO buildCommentFormDTO()
    {
        DiscussionFormDTO discussionFormDTO = discussionFormDTOFactory.createEmpty(discussionKey.getType());
        discussionFormDTO.inReplyToId = discussionKey.id;
        discussionFormDTO.text = commentText.getText().toString();
        return discussionFormDTO;
    }

    public void setCommentPostedListener(CommentPostedListener listener)
    {
        this.commentPostedListener = listener;
    }

    private void detachSubmitCommentMiddleCallback()
    {
        if (postCommentMiddleCallback != null)
        {
            postCommentMiddleCallback.setPrimaryCallback(null);
        }
        postCommentMiddleCallback = null;
    }

    private void resetCommentText()
    {
        commentText.setText(null);
    }

    private void resetCommentAction()
    {
        commentActionWrapper.setDisplayedChildByLayoutId(commentSubmit.getId());
    }

    private void resetView()
    {
        resetCommentText();
        resetCommentAction();
    }

    public void linkWith(DiscussionKey discussionKey)
    {
        this.discussionKey = discussionKey;
    }

    public void linkWith(MessageType messageType)
    {
        this.messageType = messageType;
    }

    protected void setPosting()
    {
        commentActionWrapper.setDisplayedChildByLayoutId(commentActionProcessing.getId());
        commentSubmit.setEnabled(false);
        resetCommentText();
    }

    protected void setPosted()
    {
        commentActionWrapper.setDisplayedChildByLayoutId(commentSubmit.getId());
        commentSubmit.setEnabled(true);
    }

    // HACK
    protected void fixHackDiscussion(DiscussionDTO discussionDTO)
    {
        if (discussionDTO != null && discussionDTO.userId <= 0)
        {
            discussionDTO.userId = currentUserId.toUserBaseKey().key;
        }
    }

    protected class CommentSubmitCallback implements Callback<DiscussionDTO>
    {
        @Override public void success(DiscussionDTO discussionDTO, Response response)
        {
            setPosted();
            fixHackDiscussion(discussionDTO);
            if (commentPostedListener != null)
            {
                commentPostedListener.success(discussionDTO);
            }
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            setPosted();
            THToast.show(new THException(retrofitError));
            if (commentPostedListener != null)
            {
                commentPostedListener.failure(retrofitError);
            }
        }
    }

    public static interface CommentPostedListener
    {
        void success(DiscussionDTO discussionDTO);

        void failure(Exception exception);
    }
}
