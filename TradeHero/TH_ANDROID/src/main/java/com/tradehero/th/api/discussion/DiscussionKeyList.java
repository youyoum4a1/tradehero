package com.tradehero.th.api.discussion;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import java.util.Collection;

/**
 * Created by thonguyen on 4/4/14.
 */
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
}
