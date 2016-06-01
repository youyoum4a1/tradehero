package com.ayondo.academy.api.discussion;

import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTOKeyIdList;
import com.ayondo.academy.api.discussion.key.DiscussionKey;

public class DiscussionKeyList extends DTOKeyIdList<DiscussionKey>
{
    @Nullable public DiscussionKey getLowestId()
    {
        DiscussionKey lowest = null;
        for (DiscussionKey discussionKey: this)
        {
            if (lowest == null || discussionKey.id < lowest.id)
            {
                lowest = discussionKey;
            }
        }
        return lowest;
    }

    @Nullable public DiscussionKey getHighestId()
    {
        DiscussionKey highest = null;
        for (DiscussionKey discussionKey: this)
        {
            if (highest == null || highest.id < discussionKey.id)
            {
                highest = discussionKey;
            }
        }
        return highest;
    }
}
