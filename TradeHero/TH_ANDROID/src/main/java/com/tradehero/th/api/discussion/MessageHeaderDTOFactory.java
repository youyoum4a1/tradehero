package com.tradehero.th.api.discussion;

import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class MessageHeaderDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public MessageHeaderDTOFactory()
    {
        super();
    }
    //</editor-fold>

    @NotNull public MessageHeaderDTO create(@NotNull DiscussionDTO from)
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
