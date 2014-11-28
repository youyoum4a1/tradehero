package com.tradehero.chinabuild.data;

import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;

/**
 * Created by huhaiping on 14-10-26.
 */
public class EmptyDiscussionCompactDTO extends AbstractDiscussionCompactDTO
{

    @Override public DiscussionKey getDiscussionKey()
    {
        return null;
    }
}
