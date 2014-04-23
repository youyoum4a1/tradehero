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

        // TODO remove this temporary HACK
        {
            if (unidentified.type.equals(DiscussionType.COMMENT))
            {
                unidentified.type = DiscussionType.PRIVATE_MESSAGE;
            }
        }

        return createChildClass(unidentified.type, unidentified);
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
            }
        }
        return created;
    }
}
