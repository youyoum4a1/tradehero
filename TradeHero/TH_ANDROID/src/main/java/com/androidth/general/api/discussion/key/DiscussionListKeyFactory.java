package com.androidth.general.api.discussion.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.discussion.DiscussionDTO;
import com.androidth.general.api.discussion.DiscussionType;
import com.androidth.general.api.discussion.MessageHeaderDTO;
import com.androidth.general.api.users.CurrentUserId;

public class DiscussionListKeyFactory
{
    @Nullable public static DiscussionListKey create(@Nullable Bundle args)
    {
        DiscussionListKey discussionListKey = null;
        if (args != null)
        {
            // Perhaps a bit too haphazard?
            if (args.containsKey(DiscussionVoteKey.VOTE_DIRECTION_NAME_BUNDLE_KEY))
            {
                discussionListKey = new DiscussionVoteKey(args);
            }
            else if (args.containsKey(RangedDiscussionListKey.MAX_COUNT_BUNDLE_KEY))
            {
                discussionListKey = new RangedDiscussionListKey(args);
            }
            else if (args.containsKey(PaginatedDiscussionListKey.PAGE_BUNDLE_KEY))
            {
                discussionListKey = new PaginatedDiscussionListKey(args);
            }
            else if (args.containsKey(DiscussionListKey.IN_REPLY_TO_TYPE_NAME_BUNDLE_KEY))
            {
                discussionListKey = new DiscussionListKey(args);
            }
        }
        return discussionListKey;
    }

    @Nullable public static DiscussionListKey create(
            @Nullable DiscussionDTO discussionDTO,
            @NonNull CurrentUserId currentUserId)
    {
        if (discussionDTO == null || discussionDTO.type == null)
        {
            return null;
        }
        if (discussionDTO.type.equals(DiscussionType.PRIVATE_MESSAGE))
        {
            DiscussionType discussionType = discussionDTO.inReplyToType != null ?
                    discussionDTO.inReplyToType :
                    discussionDTO.type;
            int inReplyToId = discussionDTO.inReplyToId != 0 ?
                    discussionDTO.inReplyToId :
                    discussionDTO.id;
            return new MessageDiscussionListKey(
                    discussionType,
                    inReplyToId,
                    discussionDTO.getSenderKey(),
                    currentUserId.toUserBaseKey(),
                    null, null, null);
        }
        return create(discussionDTO.getDiscussionKey());
    }

    @Nullable public static DiscussionListKey create(@NonNull DiscussionKey discussionKey)
    {
        return new DiscussionListKey(discussionKey.getType(), discussionKey.id);
    }

    @Nullable public static DiscussionListKey create(@Nullable MessageHeaderDTO messageHeaderDTO)
    {
        if (messageHeaderDTO == null || messageHeaderDTO.discussionType == null)
        {
            return null;
        }

        switch (messageHeaderDTO.discussionType)
        {
            case BROADCAST_MESSAGE:
            case PRIVATE_MESSAGE:
                return new MessageDiscussionListKey(
                        messageHeaderDTO.discussionType,
                        messageHeaderDTO.id,
                        messageHeaderDTO.getSenderId(),
                        messageHeaderDTO.getRecipientId(),
                        null, null, null);

            case COMMENT:
            case TIMELINE_ITEM:
            case SECURITY:
            case NEWS:
                return new PaginatedDiscussionListKey(
                        messageHeaderDTO.discussionType,
                        messageHeaderDTO.id,
                        null, null);

            default:
                throw new IllegalArgumentException("Unhandled type");
        }
    }
}
