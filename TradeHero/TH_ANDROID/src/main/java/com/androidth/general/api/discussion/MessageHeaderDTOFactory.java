package com.androidth.general.api.discussion;

import android.support.annotation.NonNull;

public class MessageHeaderDTOFactory
{
    @NonNull public static MessageHeaderDTO create(@NonNull DiscussionDTO from)
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
