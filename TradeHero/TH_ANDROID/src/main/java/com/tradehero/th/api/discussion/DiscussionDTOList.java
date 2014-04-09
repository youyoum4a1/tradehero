package com.tradehero.th.api.discussion;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by xavier2 on 2014/4/9.
 */
public class DiscussionDTOList extends ArrayList<DiscussionDTO>
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

    public DiscussionDTOList(Collection<? extends DiscussionDTO> discussionDTOs)
    {
        super(discussionDTOs);
    }
    //</editor-fold>

    public DiscussionKeyList getKeys()
    {
        DiscussionKeyList keyList = new DiscussionKeyList();
        for (DiscussionDTO discussionDTO : this)
        {
            keyList.add(discussionDTO.getDiscussionKey());
        }
        return keyList;
    }
}
