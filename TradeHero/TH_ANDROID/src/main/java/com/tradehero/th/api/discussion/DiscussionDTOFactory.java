package com.tradehero.th.api.discussion;

import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;

public class DiscussionDTOFactory
{
    @Inject UserProfileCache userProfileCache;

    @Inject public DiscussionDTOFactory()
    {
        super();
    }

    public DiscussionDTO createChildClass(DiscussionDTO unidentified)
    {
        if (unidentified == null)
        {
            return null;
        }
        return createChildClass(unidentified.inReplyToType, unidentified);
    }

    public DiscussionDTO createChildClass(DiscussionType discussionType, DiscussionDTO discussionDTO)
    {
        DiscussionDTO created = discussionDTO;
        if (discussionType != null)
        {
            switch (discussionType)
            {
                case PRIVATE_MESSAGE:
                    created = new PrivateDiscussionDTO();
                    if (discussionDTO != null)
                    {
                        ((PrivateDiscussionDTO) created).putAll(discussionDTO, PrivateDiscussionDTO.class);
                    }
                    break;

                default:
                    created = new DiscussionDTO();
            }
        }
        return created;
    }

    /**
     * This message is useful to pretend that the message header is the first item of the discussion.
     * Example in private messages.
     * @param messageHeaderDTO
     * @return
     */
    public DiscussionDTO createFrom(MessageHeaderDTO messageHeaderDTO)
    {
        if (messageHeaderDTO == null)
        {
            return null;
        }
        DiscussionDTO discussionDTO = createChildClass(messageHeaderDTO.discussionType, null);
        discussionDTO.inReplyToType = messageHeaderDTO.discussionType;
        discussionDTO.inReplyToId = messageHeaderDTO.id;
        discussionDTO.createdAtUtc = messageHeaderDTO.createdAtUtc;
        discussionDTO.text = messageHeaderDTO.message;
        discussionDTO.userId = messageHeaderDTO.senderUserId;
        return discussionDTO;
    }
}
