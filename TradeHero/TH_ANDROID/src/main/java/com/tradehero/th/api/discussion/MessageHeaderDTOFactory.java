package com.tradehero.th.api.discussion;

import javax.inject.Inject;
import android.support.annotation.NonNull;

public class MessageHeaderDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public MessageHeaderDTOFactory()
    {
        super();
    }
    //</editor-fold>

    @NonNull public MessageHeaderDTO create(@NonNull DiscussionDTO from)
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
