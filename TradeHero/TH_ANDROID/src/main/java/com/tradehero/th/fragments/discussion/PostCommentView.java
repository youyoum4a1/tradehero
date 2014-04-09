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
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;

/**
 * Created by thonguyen on 9/4/14.
 */
public class PostCommentView extends RelativeLayout
    implements DTOView<AbstractDiscussionDTO>
{
    @InjectView(R.id.post_comment_action_submit) TextView commentSubmit;
    @InjectView(R.id.post_comment_text) EditText commentText;

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
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @OnClick(R.id.post_comment_action_submit) void postComment()
    {
        //detachCommentSubmitMiddleCallback();
        //
        //DiscussionDTO discussionDTO = new DiscussionDTO();
        //discussionDTO.text = commentText.getText().toString();
        //discussionDTO.inReplyToType = DiscussionType.TIMELINE_ITEM;
        //discussionDTO.inReplyToId = discussionKey.key;
        //
        //resetCommentText();
        //discussionMiddleCallback = discussionServiceWrapper.createDiscussion(discussionDTO, new CommentSubmitCallback());
    }

    private void resetCommentText()
    {
        commentText.setText(null);
    }

    @Override public void display(AbstractDiscussionDTO dto)
    {
        // TODO
    }
}
