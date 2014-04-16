package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.discussion.MessageStatusDTO;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.fragments.discussion.PostCommentView;

public class PrivatePostCommentView extends PostCommentView
{
    private OnMessageNotAllowedToSendListener messageNotAllowedToSendListener;
    private MessageStatusDTO messageStatusDTO;
    
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

    public void setMessageNotAllowedToSendListener(OnMessageNotAllowedToSendListener messageNotAllowedToSendListener)
    {
        this.messageNotAllowedToSendListener = messageNotAllowedToSendListener;
    }

    public void setMessageStatusDTO(MessageStatusDTO messageStatusDTO)
    {
        this.messageStatusDTO = messageStatusDTO;
    }

    /**
     * Here we want to make sure we are allowed to post a comment before doing it
     */
    @Override protected void postComment()
    {
        //TODO need replace false
        if (messageStatusDTO != null && false &&
        //if (messageStatusDTO != null && messageStatusDTO.privateFreeRemainingCount <= 0 &&
                messageNotAllowedToSendListener != null)
        {
            notifyPreSubmissionInterceptListener();
        }
        else 
        {
            super.postComment();
        }
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
