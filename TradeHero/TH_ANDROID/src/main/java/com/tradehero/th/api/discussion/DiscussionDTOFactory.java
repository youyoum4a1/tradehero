package com.tradehero.th.api.discussion;

import javax.inject.Inject;

public class DiscussionDTOFactory
{
    @Inject public DiscussionDTOFactory()
    {
        super();
    }

    public DiscussionDTO createChildClass(DiscussionDTO unidentified)
    {
        if (unidentified != null && unidentified.inReplyToType != null)
        {
            switch (unidentified.inReplyToType)
            {
                case PRIVATE_MESSAGE:
                    unidentified = new PrivateDiscussionDTO(unidentified, PrivateDiscussionDTO.class);
                    break;
            }
        }
        return unidentified;
    }
}
