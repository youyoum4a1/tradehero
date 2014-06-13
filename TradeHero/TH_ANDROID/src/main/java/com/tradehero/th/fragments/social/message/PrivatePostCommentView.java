package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import com.tradehero.th.api.discussion.form.PrivateMessageCreateFormDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.discussion.PostCommentView;
import org.jetbrains.annotations.NotNull;

public class PrivatePostCommentView extends PostCommentView
{
    private UserBaseKey recipient;

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

    @Override protected DiscussionType getDefaultDiscussionType()
    {
        return DiscussionType.PRIVATE_MESSAGE;
    }

    public void setRecipient(@NotNull UserBaseKey recipient)
    {
        this.recipient = recipient;
    }

    @NotNull
    @Override protected MessageCreateFormDTO buildMessageCreateFormDTO()
    {
        @NotNull MessageCreateFormDTO message = super.buildMessageCreateFormDTO();
        if (recipient != null)
        {
            ((PrivateMessageCreateFormDTO) message).recipientUserId = recipient.key;
        }
        return message;
    }

    @Override protected DiscussionFormDTO buildCommentFormDTO()
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
