package com.tradehero.th.api.discussion;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.KeyGenerator;
import com.tradehero.th.api.discussion.key.MessageId;
import java.util.Date;
import java.util.Random;

public class MessageDTO implements DTO, KeyGenerator
{
    private static final Random idGenerator = new Random();

    // TODO ask for an id from server
    public int id = idGenerator.nextInt(Integer.MAX_VALUE);

    public String title;
    public String subTitle;
    public String message;
    public MessageType messageType;
    public Integer recipientUserId;

    // set by API when return to client
    public int senderUserId;
    public Date createdAtUtc;
    public Integer commentId;
    public DiscussionType discussionType;
    public String imageUrl;

    public MessageDTO()
    {
        super();
    }

    @Deprecated
    public MessageDTO(String title, String subTitle, String message, Date createdAtUtc)
    {
        this.title = title;
        this.subTitle = subTitle;
        this.message = message;
        this.createdAtUtc = createdAtUtc;
    }

    @Override public MessageId getDTOKey()
    {
        return new MessageId(id);
    }
}
