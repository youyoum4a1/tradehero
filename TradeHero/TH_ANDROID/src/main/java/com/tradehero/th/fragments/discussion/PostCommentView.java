package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.EditableUtil;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.form.DiscussionFormDTOFactory;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTOFactory;
import com.tradehero.th.api.discussion.form.ReplyDiscussionFormDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.utils.DeviceUtil;
import javax.inject.Inject;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;

/**
 * A layout that often will be included in a discussion, consists of a TextView for input discussion
 * comment and a submit button.
 */
public class PostCommentView extends RelativeLayout
{
    /**
     * If false, then we wait for a return from the server before adding the discussion. If true, we add it right away.
     */
    public static final boolean USE_QUICK_STUB_DISCUSSION = true;
    private boolean keypadIsShowing;

    @InjectView(R.id.post_comment_action_submit) TextView commentSubmit;
    @InjectView(R.id.post_comment_action_processing) View commentActionProcessing;
    @InjectView(R.id.post_comment_action_wrapper) BetterViewAnimator commentActionWrapper;
    @InjectView(R.id.post_comment_text) EditText commentText;

    @NonNull private SubscriptionList postCommentSubscriptions;

    @Inject MessageServiceWrapper messageServiceWrapper;
    @Nullable private MessageType messageType = null;
    @Inject CurrentUserId currentUserId;

    @Inject DiscussionServiceWrapper discussionServiceWrapper;
    private DiscussionKey discussionKey = null;
    private CommentPostedListener commentPostedListener;
    private DiscussionKey nextStubKey;

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
        HierarchyInjector.inject(this);
        postCommentSubscriptions = new SubscriptionList();
        keypadIsShowing = false;
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (commentText != null)
        {
            commentText.setOnFocusChangeListener(createEditTextFocusChangeListener());
            commentText.requestFocus();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        postCommentSubscriptions.unsubscribe();
        resetView();
        commentText.setOnFocusChangeListener(null);
        commentPostedListener = null;

        DeviceUtil.dismissKeyboard(commentText);
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void dismissKeypad()
    {
        if (keypadIsShowing)
        {
            DeviceUtil.dismissKeyboard(commentText);
            keypadIsShowing = false;
            commentText.clearFocus();
        }
    }

    @NonNull protected DiscussionType getDefaultDiscussionType()
    {
        return DiscussionType.COMMENT;
    }

    @NonNull public synchronized DiscussionKey moveNextStubKey()
    {
        if (nextStubKey != null)
        {
            nextStubKey = DiscussionKeyFactory.create(nextStubKey.getType(), nextStubKey.id + 1);
        }
        else if (discussionKey != null)
        {
            nextStubKey = DiscussionKeyFactory.create(discussionKey.getType(), Integer.MAX_VALUE - 10000);
        }
        else
        {
            nextStubKey = DiscussionKeyFactory.create(getDefaultDiscussionType(), Integer.MAX_VALUE - 10000);
        }
        return nextStubKey;
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.post_comment_action_submit)
    protected void postComment()
    {
        if (!validate())
        {
            THToast.show(R.string.error_empty_comment);
        }
        else if (discussionKey != null)
        {
            submitAsDiscussionReply();
        }
        else if (messageType != null)
        {
            submitAsNewDiscussion();
        }
        else
        {
            THToast.show(R.string.error_not_enough_information);
        }
    }

    protected boolean validate()
    {
        String comment = commentText.getText().toString();
        return !comment.trim().isEmpty();
    }

    protected void submitAsDiscussionReply()
    {
        DiscussionFormDTO discussionFormDTO = buildCommentFormDTO();
        setPosting();
        postCommentSubscriptions.add(
                discussionServiceWrapper.createDiscussionRx(discussionFormDTO)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(createCommentSubmitObserver()));
    }

    @NonNull protected DiscussionFormDTO buildCommentFormDTO()
    {
        DiscussionFormDTO discussionFormDTO = createEmptyCommentFormDTO();
        populateFormDTO(discussionFormDTO);
        return discussionFormDTO;
    }

    @NonNull protected DiscussionFormDTO createEmptyCommentFormDTO()
    {
        return DiscussionFormDTOFactory.createEmpty(discussionKey.getType());
    }

    protected void populateFormDTO(@NonNull DiscussionFormDTO discussionFormDTO)
    {
        if (discussionFormDTO instanceof ReplyDiscussionFormDTO)
        {
            ((ReplyDiscussionFormDTO) discussionFormDTO).inReplyToId = discussionKey.id;
        }
        discussionFormDTO.text = EditableUtil.unSpanText(commentText.getText()).toString();
        if (USE_QUICK_STUB_DISCUSSION)
        {
            discussionFormDTO.stubKey = moveNextStubKey();
        }
    }

    protected void submitAsNewDiscussion()
    {
        MessageCreateFormDTO messageCreateFormDTO = buildMessageCreateFormDTO();
        setPosting();
        postCommentSubscriptions.add(
                messageServiceWrapper.createMessageRx(messageCreateFormDTO)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(createCommentSubmitObserver()));
    }

    @NonNull protected MessageCreateFormDTO buildMessageCreateFormDTO()
    {
        MessageCreateFormDTO messageCreateFormDTO = MessageCreateFormDTOFactory.createEmpty(messageType);
        messageCreateFormDTO.message = EditableUtil.unSpanText(commentText.getText()).toString();
        messageCreateFormDTO.senderUserId = currentUserId.toUserBaseKey().key;
        return messageCreateFormDTO;
    }

    public void setCommentPostedListener(CommentPostedListener listener)
    {
        this.commentPostedListener = listener;
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
        dismissKeypad();
    }

    protected void setPosted()
    {
        commentActionWrapper.setDisplayedChildByLayoutId(commentSubmit.getId());
        commentSubmit.setEnabled(true);
    }

    protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        setPosted();
        notifyCommentPosted(discussionDTO);
    }

    protected void notifyCommentPosted(DiscussionDTO discussionDTO)
    {
        CommentPostedListener commentPostedListenerCopy = commentPostedListener;
        if (commentPostedListenerCopy != null)
        {
            commentPostedListenerCopy.success(discussionDTO);
        }
    }

    protected void notifyCommentPostFailed(Exception exception)
    {
        CommentPostedListener commentPostedListenerCopy = commentPostedListener;
        if (commentPostedListenerCopy != null)
        {
            commentPostedListenerCopy.failure(exception);
        }
    }

    protected OnFocusChangeListener createEditTextFocusChangeListener()
    {
        return new PostCommentViewEditTextFocusChangeListener();
    }

    protected class PostCommentViewEditTextFocusChangeListener implements OnFocusChangeListener
    {
        @Override public void onFocusChange(View v, boolean hasFocus)
        {
            if (hasFocus)
            {
                keypadIsShowing = true;
                ((FragmentActivity) getContext()).getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }
    }

    protected Observer<DiscussionDTO> createCommentSubmitObserver()
    {
        return new CommentSubmitObserver();
    }

    protected class CommentSubmitObserver implements Observer<DiscussionDTO>
    {
        @Override public void onNext(DiscussionDTO discussionDTO)
        {
            handleCommentPosted(discussionDTO);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            setPosted();
            THToast.show(new THException(e));
            notifyCommentPostFailed((Exception) e);
        }
    }

    public interface CommentPostedListener
    {
        void success(DiscussionDTO discussionDTO);

        void failure(Exception exception);
    }
}
