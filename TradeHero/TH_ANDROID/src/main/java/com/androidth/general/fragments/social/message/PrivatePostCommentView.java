package com.androidth.general.fragments.social.message;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.api.discussion.DiscussionType;
import com.androidth.general.api.discussion.MessageType;
import com.androidth.general.api.discussion.form.DiscussionFormDTO;
import com.androidth.general.api.discussion.form.MessageCreateFormDTO;
import com.androidth.general.api.discussion.form.PrivateMessageCreateFormDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.fragments.discussion.PostCommentView;

public class PrivatePostCommentView extends PostCommentView
{
    private UserBaseKey recipient;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public PrivatePostCommentView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public PrivatePostCommentView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
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

    @NonNull @Override protected DiscussionType getDefaultDiscussionType()
    {
        return DiscussionType.PRIVATE_MESSAGE;
    }

    public void setRecipient(@NonNull UserBaseKey recipient)
    {
        this.recipient = recipient;
    }

    @NonNull
    @Override protected MessageCreateFormDTO buildMessageCreateFormDTO()
    {
        MessageCreateFormDTO message = super.buildMessageCreateFormDTO();
        if (recipient != null)
        {
            ((PrivateMessageCreateFormDTO) message).recipientUserId = recipient.key;
        }
        return message;
    }

    @NonNull @Override protected DiscussionFormDTO buildCommentFormDTO()
    {
        DiscussionFormDTO discussionFormDTO = super.buildCommentFormDTO();
        if (recipient != null)
        {
            discussionFormDTO.recipientUserId = recipient.key;
        }
        else
        {
            THToast.show(R.string.discussion_error_setting_recipient);
        }
        return discussionFormDTO;
    }
}
