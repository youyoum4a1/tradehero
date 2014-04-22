package com.tradehero.th.api.discussion;

import javax.inject.Inject;

public class MessageHeaderDTOFactory
{
    @Inject public MessageHeaderDTOFactory()
    {
        super();
    }

    public MessageHeaderDTO create(DiscussionDTO from)
    {
        MessageHeaderDTO created = new MessageHeaderDTO();
        created.discussionType = from.type;
        created.id = from.id;
        created.title = from.text;
        created.senderUserId = from.userId;
        created.createdAtUtc = from.createdAtUtc;
        return created;
    }
}
