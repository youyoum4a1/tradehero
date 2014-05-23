package com.tradehero.th.api.discussion;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.KeyGenerator;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.discussion.key.MessageHeaderUserId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.utils.DaggerUtils;
import java.util.Date;
import java.util.Random;
import javax.inject.Inject;

public class MessageHeaderDTO implements DTO, KeyGenerator
{
    private static final Random idGenerator = new Random();

    @Inject CurrentUserId currentUserId;

    // must be set by client when creating new message
    public String title;
    public String subTitle;
    public String message;
    public MessageType messageType;
    public Integer recipientUserId;

    // set by API when return to client
    public int senderUserId;
    public Date createdAtUtc;
    public Integer id;  // is always a commentId, i.e. [comment].id
    public DiscussionType discussionType;
    public String imageUrl;
    public boolean unread;

    public String latestMessage;
    public Date latestMessageAtUtc;

    //<editor-fold desc="Constructors">
    public MessageHeaderDTO()
    {
        super();
        init();
    }

    @Deprecated
    public MessageHeaderDTO(String title, String subTitle, String message, Date createdAtUtc)
    {
        this(title, subTitle, message, createdAtUtc, false);
    }

    @Deprecated
    public MessageHeaderDTO(String title, String subTitle, String message, Date createdAtUtc, boolean unread)
    {
        this.id = idGenerator.nextInt(Integer.MAX_VALUE);
        this.title = title;
        this.subTitle = subTitle;
        this.message = message;
        this.createdAtUtc = createdAtUtc;
        this.unread = unread;
        init();
    }
    //</editor-fold>

    protected void init()
    {
        DaggerUtils.inject(this);
    }

    @Override public MessageHeaderId getDTOKey()
    {
        UserBaseKey correspondentId = getCorrespondentId(currentUserId.toUserBaseKey());
        if (correspondentId != null)
        {
            return new MessageHeaderUserId(id, correspondentId);
        }
        return new MessageHeaderId(id);
    }

    public UserBaseKey getSenderId()
    {
        return new UserBaseKey(senderUserId);
    }

    public UserBaseKey getRecipientId()
    {
        if (recipientUserId != null)
        {
            return new UserBaseKey(recipientUserId);
        }
        return null;
    }

    /**
     * Returns the user id that is not the currentUserId parameter.
     * @param currentUserId
     * @return
     */
    public UserBaseKey getCorrespondentId(UserBaseKey currentUserId)
    {
        UserBaseKey senderId = getSenderId();
        if (!senderId.equals(currentUserId))
        {
            return senderId;
        }
        return getRecipientId();
    }

    @Override public String toString()
    {
        return "MessageHeaderDTO{" +
                "title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", message='" + message + '\'' +
                ", messageType=" + messageType +
                ", recipientUserId=" + recipientUserId +
                ", senderUserId=" + senderUserId +
                ", createdAtUtc=" + createdAtUtc +
                ", id=" + id +
                ", discussionType=" + discussionType +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
