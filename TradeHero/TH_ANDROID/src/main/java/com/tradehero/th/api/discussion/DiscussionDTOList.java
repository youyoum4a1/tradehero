package com.tradehero.th.api.discussion;

import java.util.ArrayList;
import java.util.Collection;

public class DiscussionDTOList<T extends AbstractDiscussionCompactDTO> extends ArrayList<T>
{
    //<editor-fold desc="Constructors">
    public DiscussionDTOList(int i)
    {
        super(i);
    }

    public DiscussionDTOList()
    {
        super();
    }

    public DiscussionDTOList(Collection<T> discussionDTOs)
    {
        super(discussionDTOs);
    }
    //</editor-fold>

    public DiscussionKeyList getKeys()
    {
        DiscussionKeyList keyList = new DiscussionKeyList();
        for (AbstractDiscussionCompactDTO discussionDTO : this)
        {
            keyList.add(discussionDTO.getDiscussionKey());
        }
        return keyList;
    }
}
