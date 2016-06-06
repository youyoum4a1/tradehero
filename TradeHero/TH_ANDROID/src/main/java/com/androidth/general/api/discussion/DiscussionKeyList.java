package com.androidth.general.api.discussion;

import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.DTOKeyIdList;
import com.androidth.general.api.discussion.key.DiscussionKey;

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
