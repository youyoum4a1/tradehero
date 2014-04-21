package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import com.tradehero.th.api.discussion.form.PrivateMessageCreateFormDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.fragments.discussion.PostCommentView;

public class PrivatePostCommentView extends PostCommentView
{
    private OnMessageNotAllowedToSendListener messageNotAllowedToSendListener;
    private UserBaseKey recipient;
    private UserMessagingRelationshipDTO userMessagingRelationshipDTO;

    //<editor-fold desc="Constructors">
    public PrivatePostCommentView(Context context)
    {
        super(context);
    }

    public PrivatePostCommentView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PrivatePostCommentView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        linkWith(MessageType.PRIVATE);
    }

    @Override protected void onDetachedFromWindow()
    {
        this.messageNotAllowedToSendListener = null;
        super.onDetachedFromWindow();
    }

    public void setMessageNotAllowedToSendListener(
            OnMessageNotAllowedToSendListener messageNotAllowedToSendListener)
    {
        this.messageNotAllowedToSendListener = messageNotAllowedToSendListener;
    }

    public void setRecipient(UserBaseKey recipient)
    {
        this.recipient = recipient;
    }

    public void setUserMessagingRelationshipDTO(
            UserMessagingRelationshipDTO userMessagingRelationshipDTO)
    {
        this.userMessagingRelationshipDTO = userMessagingRelationshipDTO;
    }

    /**
     * Here we want to make sure we are allowed to post a comment before doing it
     */
    @Override protected void postComment()
    {
        if (canSendMessage())
        {
            super.postComment();
        }
        else
        {
            notifyPreSubmissionInterceptListener();
        }
    }

    protected boolean canSendMessage()
    {
        return userMessagingRelationshipDTO == null || userMessagingRelationshipDTO.canSendPrivate();
    }

    @Override protected MessageCreateFormDTO buildMessageCreateFormDTO()
    {
        MessageCreateFormDTO message = super.buildMessageCreateFormDTO();
        ((PrivateMessageCreateFormDTO) message).recipientUserId = recipient.key;
        return message;
    }

    protected void notifyPreSubmissionInterceptListener()
    {
        OnMessageNotAllowedToSendListener listener = messageNotAllowedToSendListener;
        if (listener != null)
        {
            listener.onMessageNotAllowedToSend();
        }
    }

    public static interface OnMessageNotAllowedToSendListener
    {
        void onMessageNotAllowedToSend();
    }
}
