package com.tradehero.th.api.discussion;

import com.tradehero.th.api.discussion.key.PrivateMessageKey;

public class PrivateDiscussionDTO extends AbstractDiscussionDTO
{
    public PrivateDiscussionDTO()
    {
        super();
    }

    @Override public PrivateMessageKey getDiscussionKey()
    {
        return new PrivateMessageKey(id);
    }
}
