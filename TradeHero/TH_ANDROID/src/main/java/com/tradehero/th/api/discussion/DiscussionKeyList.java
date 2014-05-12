package com.tradehero.th.api.discussion;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import java.util.Collection;

public class DiscussionKeyList extends DTOKeyIdList<DiscussionKey>
{
    //<editor-fold desc="Constructors">
    public DiscussionKeyList()
    {
        super();
    }

    public DiscussionKeyList(int capacity)
    {
        super(capacity);
    }

    public DiscussionKeyList(Collection<? extends DiscussionKey> collection)
    {
        super(collection);
    }
    //</editor-fold>

    public DiscussionKey getLowestId()
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

    public DiscussionKey getHighestId()
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
