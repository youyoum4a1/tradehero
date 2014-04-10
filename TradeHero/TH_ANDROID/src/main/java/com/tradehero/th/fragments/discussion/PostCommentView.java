package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by thonguyen on 9/4/14.
 */
public class PostCommentView extends RelativeLayout
    implements DTOView<DiscussionKey>
{
    @InjectView(R.id.post_comment_action_submit) TextView commentSubmit;
    @InjectView(R.id.post_comment_text) EditText commentText;

    @Inject DiscussionServiceWrapper discussionServiceWrapper;

    private DiscussionKey discussionKey;

    private MiddleCallback<DiscussionDTO> postCommentMiddleCallback;
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

        commentPostedListener = null;

        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @OnClick(R.id.post_comment_action_submit) void postComment()
    {
        detachSubmitCommentMiddleCallback();

        if (discussionKey != null)
        {
            DiscussionDTO discussionDTO = buildCommentFormDTO();

            setPosting();
            postCommentMiddleCallback = discussionServiceWrapper.createDiscussion(discussionDTO, new CommentSubmitCallback());
        }
    }

    private DiscussionDTO buildCommentFormDTO()
    {
        DiscussionDTO discussionDTO = new DiscussionDTO();

        discussionDTO.inReplyToId = discussionKey.id;
        discussionDTO.inReplyToType = discussionKey.type;
        discussionDTO.text = commentText.getText().toString();
        return discussionDTO;
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

    @Override public void display(DiscussionKey discussionKey)
    {
        this.discussionKey = discussionKey;
    }

    private class CommentSubmitCallback implements Callback<DiscussionDTO>
    {
        @Override public void success(DiscussionDTO discussionDTO, Response response)
        {
            setPosted();

            if (commentPostedListener != null)
            {
                commentPostedListener.success(discussionDTO);
            }
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            setPosted();

            if (commentPostedListener != null)
            {
                commentPostedListener.failure();
            }
        }
    }

    protected void setPosting()
    {
        commentSubmit.setEnabled(false);

        resetCommentText();
    }

    protected void setPosted()
    {
        commentSubmit.setEnabled(false);
    }

    public static interface CommentPostedListener
    {
        void success(DiscussionDTO discussionDTO);

        void failure();
    }
}
